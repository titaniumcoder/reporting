package io.github.titaniumcoder.toggl.reporting.toggl

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class TogglClient(config: TogglConfiguration) {
    val apiToken = config.apiToken
    val workspaceId = config.workspaceId

    val log = LoggerFactory.getLogger(TogglClient::class.java)

    val userAgent = "https://github.com/titaniumcoder/toggl-reporting"

    val authHeader by lazy {
        "Basic " + Base64.getEncoder().encodeToString("$apiToken:api_token".toByteArray())
    }

    // This method returns filter function which will log request data
    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            Mono.just<ClientRequest>(clientRequest)
        }
    }

    val client = WebClient
            .builder()
            .baseUrl("https://toggl.com/")
            .filter(logRequest()) // here is the magic
            .build()

    fun clients(): Flux<TogglModel.Client> =
            client
                    .get()
                    .uri("https://www.toggl.com/api/v8/clients")
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToFlux(TogglModel.Client::class.java)

    fun summary(from: LocalDate, to: LocalDate): Mono<TogglModel.TogglSummary> =
        client
                .get()
                .uri("https://toggl.com/reports/api/v2/summary?user_agent={userAgent}&since={since}&until={until}&tag_ids=0&grouping=clients&subgrouping=projects&subgrouping_ids=false&workspace_id={workspaceId}",
                        mapOf(
                                "userAgent" to userAgent,
                                "workspaceId" to workspaceId.toString(),
                                "since" to from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                "until" to to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                )
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(TogglModel.TogglSummary::class.java)

    fun entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean, page: Int = 1): TogglModel.TogglReporting = TODO()

    fun tagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        TODO()
    }

    fun untagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        TODO()
    }

    fun tagBilled(entryId: Long) {
        TODO()
    }

    fun untagBilled(entryId: Long) {
        TODO()
    }
}


// original implementation:
/*
class TogglWsClient @Inject()(ws: WSClient,
                              config: Configuration
                             )(implicit ec: ExecutionContext)
  extends TogglClient {

  lazy val apiToken = config.get[String]("apiToken")
  lazy val workspaceId = config.get[Int]("workspaceId")

  def prepareWs(url: String) =
    ws.url(url)
      // .withRequestFilter(AhcCurlRequestLogger())
      .withAuth(apiToken, "api_token", WSAuthScheme.BASIC)

  private def mergeReportings(l: List[TogglReporting]): TogglReporting =
    l.reduceLeft { (a, b) => a.copy(data = a.data ++ b.data) }

  override def entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean, page: Int): Future[TogglReporting] = {
    def onePage(pageNo: Int): Future[TogglReporting] =
      prepareWs("https://toggl.com/reports/api/v2/details")
        .addQueryStringParameters(
          "user_agent" -> "https://github.com/titaniumcoder/reporting-play-scala",
          "workspace_id" -> workspaceId.toString,
          "since" -> from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
          "until" -> to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
          "client_ids" -> clientId.toString,
          "display_hours" -> "minutes",
          "page" -> pageNo.toString
        )
        .get()
        .map {
          response => response.json.as[TogglReporting]
        }

    val allPages = for {
      firstPage <- onePage(page)
      pageCount = Math.ceil((0.0 + firstPage.totalCount) / firstPage.perPage).toInt
      pages = 2 to pageCount
      remaining <- Future.sequence(pages.map(onePage))
    } yield firstPage :: remaining.toList

    implicit val o: Ordering[LocalDateTime] = (x, y) => x.toEpochSecond(ZoneOffset.UTC).compareTo(y.toEpochSecond(ZoneOffset.UTC))

    allPages
      .map(mergeReportings)
      .map(v => v.copy(data = v.data
        .filter(x => !nonBilledOnly || !x.tags.contains("billed"))
        .sortBy(x => (x.client.getOrElse(""), x.start))
      ))
  }

  private def tagId(ids: String, body: JsObject): Future[Int] = {
    prepareWs(s"https://www.toggl.com/api/v8/time_entries/$ids")
      .put(body)
      .map(response => response.status)
  }

  private def tagBody(remove: Boolean) = Json.obj(
    "time_entry" ->
      Json.obj(
        "tags" -> (if (remove) Json.arr() else Json.arr("billed"))
      )
  )

  // TODO this is a dirty hack until toggl fixes it's remove tags api...

  sealed trait BilledAction

  case object Add extends BilledAction
  case object Remove extends BilledAction

  private def tagRange(clientId: Long, from: LocalDate, to: LocalDate, action: BilledAction): Future[Unit] = {
    // TODO error handling decision still needs to be done because I ignore currently the result of the successful call...
    val body = tagBody(remove = action == Remove)

    entries(clientId = clientId, from = from, to = to, nonBilledOnly = false)
      .map(_.data.map(r => r.id).grouped(50).map(z => z.mkString(",")).toList)
      .flatMap(ids => Future.sequence(ids.map(id => tagId(id, body))))
      .map(statu => statu.forall(_ == 200))
      .flatMap {
        case true => Future.unit
        case false => Future.failed(new RuntimeException("Update failed for some pages"))
      }
  }

  override def tagBilled(clientId: Long, from: LocalDate, to: LocalDate): Future[Unit] = tagRange(clientId, from, to, Add)
  override def untagBilled(clientId: Long, from: LocalDate, to: LocalDate): Future[Unit] = tagRange(clientId, from, to, Remove)
  override def tagBilled(entryId: Long) = tagId(entryId.toString, tagBody(remove = false)).map(_ => ())
  override def untagBilled(entryId: Long) = tagId(entryId.toString, tagBody(remove = true)).map(_ => ())
}
 */