package io.github.titaniumcoder.toggl.reporting.toggl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class TogglWebClient(config: TogglConfiguration) {
    private val apiToken = config.apiToken
    private val workspaceId = config.workspaceId

    private val log: Logger = LoggerFactory.getLogger(TogglWebClient::class.java)

    private val userAgent = "https://github.com/titaniumcoder/toggl-reporting"

    val client = WebClient
            .builder()
            .baseUrl("https://toggl.com/")
            .filter(ExchangeFilterFunctions.basicAuthentication(apiToken, "api_token"))
            .filter(logRequest()) // here is the magic
            .build()

    suspend fun clients(): List<TogglModel.Client> =
            client
                    .get()
                    .uri("https://www.toggl.com/api/v8/clients")
                    .retrieve()
                    .bodyToMono(object : ParameterizedTypeReference<List<TogglModel.Client>>() {})
                    .awaitFirst()


    suspend fun summary(from: LocalDate, to: LocalDate): TogglModel.TogglSummary =
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
                    .retrieve()
                    .bodyToMono(TogglModel.TogglSummary::class.java)
                    .awaitFirst()

    suspend fun entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean, pageNo: Int): TogglModel.TogglReporting =
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
                    .retrieve()
                    .bodyToMono(TogglModel.TogglReporting::class.java)
                    .awaitFirst()

    suspend fun tagId(ids: List<String>, billed: Boolean): HttpStatus =
            client
                    .put()
                    .uri("https://www.toggl.com/api/v8/time_entries/${ids.joinToString(",")}")
                    .syncBody(tagbody(billed))
                    .exchange()
                    .map { it.statusCode() }
                    .awaitFirst()

    private fun tagbody(billed: Boolean): ObjectNode {
        val om = ObjectMapper()
        val tags = om.createArrayNode()
        if (billed) {
            tags.add("billed")
        }

        val te = om.createObjectNode()
        te.set("tags", tags)

        val on = om.createObjectNode()
        on.set("time_entry", te)

        return on
    }

    // This method returns filter function which will log request data
    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            Mono.just<ClientRequest>(clientRequest)
        }
    }
}
