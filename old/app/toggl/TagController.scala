package toggl

import java.time.LocalDate

import excel.TimesheetService
import frontend.AuthenticatedUserAction
import javax.inject.Inject
import play.api.Configuration
import play.api.mvc._
import reporting.TransformTogglToReporting

import scala.concurrent.ExecutionContext

class TagController @Inject()(cc: ControllerComponents, config: Configuration, WithBasicAuth: AuthenticatedUserAction,
                              c: TogglClient,
                              t: TransformTogglToReporting,
                              e: TimesheetService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def tagBilled(client: Long, from: LocalDate, to: LocalDate) = WithBasicAuth.async { implicit request =>
    c.tagBilled(client, from, to)
      .map(_ => NoContent)
  }

  def untagBilled(client: Long, from: LocalDate, to: LocalDate) = WithBasicAuth.async { implicit request =>
    c.untagBilled(client, from, to)
      .map(_ => NoContent)
  }

  def tagEntry(entry: Long) = WithBasicAuth.async { implicit request =>
    c.tagBilled(entry)
      .map(_ => NoContent)
  }

  def untagEntry(entry: Long) = WithBasicAuth.async { implicit request =>
    c.untagBilled(entry)
      .map(_ => NoContent)
  }
}
