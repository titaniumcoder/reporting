package reporting

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json.{Json, OFormat}

object ReportingModel {
  implicit val dateOrdering: Ordering[LocalDateTime] = Ordering.by(d => (d.getYear, d.getDayOfYear, d.getHour, d.getMinute, d.getSecond))

  case class Project(name: String, minutes: Int)

   object Project {
    implicit val projectFormat: OFormat[Project] = Json.format[Project]
  }
  case class TimeEntry(id: Long, day: LocalDate, project: Option[String], startdate: LocalDateTime, enddate: Option[LocalDateTime], minutes: Int, description: Option[String], tags: List[String])

   object TimeEntry {
     implicit val timeEntryFormat: OFormat[TimeEntry] = Json.format[TimeEntry]
   }

  case class ViewModel(client: String, clientId: Long, from: LocalDate, to: LocalDate, projects: List[Project], timeEntries: List[List[TimeEntry]])

  object ViewModel {
    implicit val viewModelFormat: OFormat[ViewModel] = Json.format[ViewModel]
  }
}
