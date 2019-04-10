package frontend

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.mvc._

import scala.util.Try

object RoutingFormats {
  implicit def localdateBind(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[LocalDate] = new QueryStringBindable[LocalDate] {
    override def bind(key: String, params: Map[String, Seq[String]]) =
      for {
        input <- stringBinder.bind(key, params)
      } yield {
        input
          .flatMap(d =>
            Try(LocalDate.parse(d))
              .toOption
              .toRight("could not parse the date"))
      }
    override def unbind(key: String, value: LocalDate) =
      value.format(DateTimeFormatter.ISO_DATE)
  }

  // implicit val localdate: PathBindableExtractor[LocalDate] = new PathBindableExtractor[LocalDate]
}
