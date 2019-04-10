package excel

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import frontend.AuthenticatedUserAction
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OWrites}
import play.api.mvc._
import reporting.ReportingModel.{Project, TimeEntry, ViewModel}
import reporting.TransformTogglToReporting
import toggl.TogglClient

import scala.concurrent.ExecutionContext

@Singleton
class ReportingController @Inject()(cc: ControllerComponents,
                                    WithBasicAuth: AuthenticatedUserAction,
                                    c: TogglClient,
                                    t: TransformTogglToReporting,
                                    e: TimesheetService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def entries(client: Long, from: Option[LocalDate], to: Option[LocalDate], tagged: Option[Boolean]) = WithBasicAuth.async { implicit request =>
    implicit val projectWrites: OWrites[Project] = Json.writes[Project]
    implicit val timeEntryWrites: OWrites[TimeEntry] = Json.writes[TimeEntry]
    implicit val jsonWrites: OWrites[ViewModel] = Json.writes[ViewModel]

    val definiteTo = to.getOrElse(LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1))
    val definiteFrom = from.getOrElse(definiteTo.withDayOfMonth(1))

    for {
      entries <- c.entries(clientId = client, from = definiteFrom, to = definiteTo, nonBilledOnly = tagged.getOrElse(false))
      t <- t.transformInput(entries, definiteFrom, definiteTo, client)
    } yield Ok(Json.toJsObject(t))
  }

  def timesheet(client: Long, from: LocalDate, to: LocalDate) = WithBasicAuth.async { implicit request =>
    for {
      entries <- c.entries(clientId = client, from = from, to = to, nonBilledOnly = true)
      transformed <- t.transformInput(entries, from, to, client)
      excelModel <- e.transformViewModelToTimesheetModel(transformed)
      excel <- e.generateExcel(excelModel)
      datum = entries.data.headOption.map(_.start.toLocalDate).getOrElse(LocalDate.now())
    } yield Ok(excel)
      .as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
      .withHeaders(CONTENT_DISPOSITION -> s"attachment; filename=${excelModel.name}-${datum.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
  }
}
