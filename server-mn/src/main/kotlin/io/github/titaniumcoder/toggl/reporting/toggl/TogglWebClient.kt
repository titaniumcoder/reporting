package io.github.titaniumcoder.toggl.reporting.toggl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.micronaut.http.HttpStatus
import java.time.LocalDate

//@Service
class TogglWebClient(configuration: TogglConfiguration) {
    companion object {
        const val userAgent = "https://github.com/titaniumcoder/toggl-reporting"
    }

//    val client = WebClient
//            .builder()
//            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
//            .defaultUriVariables(
//                    mapOf(
//                            "userAgent" to userAgent,
//                            "workspaceId" to configuration.workspaceId
//                    )
//            )
//            .defaultHeaders { it.setBasicAuth(configuration.apiToken, "api_token") }
//            .build()

    suspend fun clients(): List<TogglModel.Client> = TODO()
//            client
//                    .get()
//                    .uri("https://www.toggl.com/api/v8/clients")
//                    .awaitExchange()
//                    .awaitBody()

    suspend fun summary(from: LocalDate, to: LocalDate): TogglModel.TogglSummary = TODO()
//            client
//                    .get()
//                    .uri("https://toggl.com/reports/api/v2/summary?user_agent={userAgent}&since={since}&until={until}&tag_ids=0&grouping=clients&subgrouping=projects&subgrouping_ids=false&workspace_id={workspaceId}",
//                            mapOf(
//                                    "since" to from.format(DateTimeFormatter.ISO_DATE),
//                                    "until" to to.format(DateTimeFormatter.ISO_DATE)
//                            ))
//                    .awaitExchange()
//                    .awaitBody()

    suspend fun entries(clientId: Long, from: LocalDate, to: LocalDate, pageNo: Int): TogglModel.TogglReporting = TODO()
//            client
//                    .get()
//                    .uri("https://toggl.com/reports/api/v2/details?user_agent={userAgent}&since={since}&until={until}&workspace_id={workspaceId}&client_ids={clientIds}&display_hours=minutes&page={page}",
//                            mapOf(
//                                    "since" to from.format(DateTimeFormatter.ISO_DATE),
//                                    "until" to to.format(DateTimeFormatter.ISO_DATE),
//                                    "clientIds" to clientId,
//                                    "page" to pageNo
//                            )
//                    )
//                    .awaitExchange()
//                    .awaitBody()

    // @Put()
    suspend fun tagId(/* @PathVariable("ids") */ ids: String, body: ObjectNode): HttpStatus = TODO()
//            client
//                    .put()
//                    .uri("https://www.toggl.com/api/v8/time_entries/{ids}",
//                            mapOf(
//                                    "ids" to ids
//                            )
//                    )
//                    .bodyValue(body)
//                    .awaitExchange()
//                    .statusCode()
}

object TagCreator {
    fun tagbody(billed: Boolean): ObjectNode {
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
}
