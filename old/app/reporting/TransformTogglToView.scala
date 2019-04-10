package reporting

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class TransformTogglToView @Inject()(implicit ec: ExecutionContext) extends TransformTogglToReporting {
  import ReportingModel._
  import toggl.TogglModel._

  override def transformInput(input: TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): Future[ViewModel] = Future {
    val timeEntries =
      input.data
        .map(t =>
          TimeEntry(
            t.id,
            t.start.toLocalDate,
            t.project,
            t.start.truncatedTo(ChronoUnit.MINUTES),
            Option(t.end).map(_.truncatedTo(ChronoUnit.MINUTES)), 
            0, 
            t.description, 
            t.tags
          )
        )
        .map(e => e.copy(minutes = e.enddate.map(x => e.startdate.until(x, ChronoUnit.MINUTES).toInt).getOrElse(0)))
        .sortBy(_.startdate)

    val finalProjects = timeEntries
      .groupBy(f => f.project)
      .map {
        case (p, v) => Project(p.getOrElse("<< Undefiniertes Projekt >>"), v.map(_.minutes).sum)
      }

    implicit val localDateOrdering: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

    ViewModel(
      client = input.data.flatMap(_.client.toList).headOption.getOrElse("None"),
      clientId = clientId,
      from = from,
      to = to,
      projects = finalProjects.toList.sortBy(_.name),
      timeEntries = timeEntries.groupBy(_.day).toList.sortBy(_._1).map(_._2).toList
    )
  }
}
