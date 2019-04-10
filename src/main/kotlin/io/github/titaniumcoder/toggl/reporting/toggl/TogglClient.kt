package io.github.titaniumcoder.toggl.reporting.toggl

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.time.LocalDate
import java.time.LocalDateTime
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

    fun entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean): Mono<TogglModel.TogglReporting> {
        fun onePage(pageNo: Int): Mono<TogglModel.TogglReporting> =
                client
                        .get()
                        .uri("https://toggl.com/reports/api/v2/details?user_agent={userAgent}&since={since}&until={until}&workspace_id={workspaceId}&client_ids={clientIds}&display_hours=minutes&page={page}",
                                mapOf(
                                        "userAgent" to userAgent,
                                        "workspaceId" to workspaceId.toString(),
                                        "since" to from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                        "until" to to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                        "clientIds" to clientId.toString(),
                                        "page" to pageNo.toString()
                                )
                        )
                        .header("Authorization", authHeader)
                        .retrieve()
                        .bodyToMono(TogglModel.TogglReporting::class.java)

        val firstPage = onePage(1)
        val followingEntries = firstPage
                .map { Math.ceil(it.totalCount.toDouble() / it.perPage.toDouble()).toInt() }
                .flatMapMany { 2.rangeTo(it).toFlux() }
                .flatMap { onePage(it).toFlux() } // TODO make this parallel later

        data class ClientSort(val client: String, val start: LocalDateTime) : Comparable<ClientSort> {
            override fun compareTo(other: ClientSort): Int =
                    when {
                        client.compareTo(other.client) != 0 -> client.compareTo(other.client)
                        else -> start.compareTo(other.start)
                    }
        }

        return Flux
                .concat(firstPage, followingEntries)
                .reduce { t: TogglModel.TogglReporting, u: TogglModel.TogglReporting -> t.copy(data = t.data + u.data) }
                .map { t ->
                    t.copy(data = t.data
                            .filter { x -> !nonBilledOnly || !x.tags.contains("billed") }
                            .sortedBy { x -> ClientSort(x.client ?: "", x.start.toLocalDateTime()) }
                    )
                }
    }

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