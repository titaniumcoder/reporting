package toggl

import java.time.{LocalDate, LocalDateTime}

import play.api.libs.json._
import play.api.libs.functional.syntax._

object TogglModel {
  // -----------  Toggl Models -----------------
  case class Client(
                     id: Long,
                     wid: Long,
                     name: String,
                     notes: Option[String]
                   )

  object Client {
    // implicit val jsonWriter: OWrites[Client] = Json.writes[Client]
    implicit val jsonReader: Reads[Client] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "wid").read[Long] and
        (JsPath \ "name").read[String] and
        (JsPath \ "notes").readNullable[String]
      ) (Client.apply _)

    implicit val jsonWrites: OWrites[Client] = Json.writes[Client]
  }

  case class TogglCurrency(
                            currency: Option[String],
                            amount: Option[Double]
                          )

  object TogglCurrency {
    implicit val jsonReader: Reads[TogglCurrency] = (
      (JsPath \ "currency").readNullable[String] and
        (JsPath \ "amount").readNullable[Double]
      ) (TogglCurrency.apply _)
  }

  case class TimeEntryReporting(
                                 id: Long,
                                 pid: Option[Long],
                                 project: Option[String],
                                 client: Option[String],
                                 task: Option[String],
                                 description: Option[String],
                                 start: LocalDateTime,
                                 end: LocalDateTime,
                                 duration: Int, // duration ms
                                 billable: Float,
                                 isBillable: Boolean,
                                 currency: String, // currency
                                 tags: List[String]
                               )

  object TimeEntryReporting {
    implicit val jsonReader: Reads[TimeEntryReporting] = (
      (JsPath \ "id").read[Long] and
        (JsPath \ "pid").readNullable[Long] and
        (JsPath \ "project").readNullable[String] and
        (JsPath \ "client").readNullable[String] and
        (JsPath \ "task").readNullable[String] and
        (JsPath \ "description").readNullable[String] and
        (JsPath \ "start").read[LocalDateTime] and
        (JsPath \ "end").read[LocalDateTime] and
        (JsPath \ "dur").read[Int] and
        (JsPath \ "billable").read[Float] and
        (JsPath \ "is_billable").read[Boolean] and
        (JsPath \ "cur").read[String] and
        (JsPath \ "tags").read[List[String]]
      ) (TimeEntryReporting.apply _)
  }

  case class TogglReporting(
                             totalCount: Int,
                             perPage: Int,
                             totalGrand: Option[Long],
                             totalBillable: Option[Long],
                             totalCurrencies: List[TogglCurrency],
                             data: List[TimeEntryReporting]
                           )

  object TogglReporting {

    import TogglCurrency._
    import TimeEntryReporting._

    implicit val jsonReader: Reads[TogglReporting] = (
      (JsPath \ "total_count").read[Int] and
        (JsPath \ "per_page").read[Int] and
        (JsPath \ "total_grand").readNullable[Long] and
        (JsPath \ "total_billable").readNullable[Long] and
        (JsPath \ "total_currencies").read[List[TogglCurrency]] and
        (JsPath \ "data").read[List[TimeEntryReporting]]
      ) (TogglReporting.apply _)
  }

  case class ClientSummaryEntry(
                                 title: Option[String],
                                 time: Long,
                                 totalCurrencies: List[TogglCurrency]
                               )

  object ClientSummaryEntry {

    import ProjectSummaryEntry._

    implicit val jsonReader: Reads[ClientSummaryEntry] = (
      (JsPath \ "title" \ "client").readNullable[String] and
        (JsPath \ "time").read[Long] and
        (JsPath \ "total_currencies").read[List[TogglCurrency]]
      ) (ClientSummaryEntry.apply _)
  }

  case class ProjectSummaryEntry(
                                  title: Option[String],
                                  time: Long,
                                  currency: Option[String],
                                  sum: Option[Double],
                                  rate: Option[Double]
                                )

  object ProjectSummaryEntry {
    implicit val jsonReader: Reads[ProjectSummaryEntry] = (
      (JsPath \ "title" \ "client").readNullable[String] and
        (JsPath \ "time").read[Long] and
        (JsPath \ "cur").readNullable[String] and
        (JsPath \ "sum").readNullable[Double] and
        (JsPath \ "rate").readNullable[Double]
      ) (ProjectSummaryEntry.apply _)
  }

  case class TogglSummary(
                           totalGrand: Option[Long],
                           totalBillable: Option[Long],
                           totalCurrencies: List[TogglCurrency],
                           data: List[ClientSummaryEntry]
                         )

  object TogglSummary {

    import TogglCurrency._
    import ClientSummaryEntry._

    implicit val jsonReader: Reads[TogglSummary] = (
      (JsPath \ "total_grand").readNullable[Long] and
        (JsPath \ "total_billable").readNullable[Long] and
        (JsPath \ "total_currencies").read[List[TogglCurrency]] and
        (JsPath \ "data").read[List[ClientSummaryEntry]]
      ) (TogglSummary.apply _)
  }

  case class Cashout(
                      client: String,
                      amount: Double
                    )

  object Cashout {
    implicit val jsonWriter: OWrites[Cashout] = Json.writes[Cashout]
  }
}
