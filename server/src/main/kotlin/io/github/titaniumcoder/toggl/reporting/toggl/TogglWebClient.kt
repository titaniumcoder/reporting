package io.github.titaniumcoder.toggl.reporting.toggl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.github.titaniumcoder.toggl.reporting.toggl.TogglWebClient.Companion.userAgent
import io.micronaut.core.convert.format.Format
import io.micronaut.http.*
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Filter
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.reactivestreams.Publisher
import java.time.LocalDate

@Filter()
class TogglWebFilter(val configuration: TogglConfiguration) : HttpClientFilter {

    override fun doFilter(request: MutableHttpRequest<*>, chain: ClientFilterChain): Publisher<out HttpResponse<*>> {
        return chain.proceed(
                request
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_AGENT, userAgent)
                        .basicAuth(configuration.apiToken, "api_token")
        )
    }
}

@Client("https://www.toggl.com")
interface TogglWebClient {
    // TODO is there a better way for the workspace id?
    @Get("/api/v8/clients")
    fun clients(): List<TogglModel.Client>

    @Get("https://toggl.com/reports/api/v2/summary?user_agent=${userAgent}&since={since}&until={until}&tag_ids=0&grouping=clients&subgrouping=projects&subgrouping_ids=false&workspace_id={workspaceId}")
    fun summary(@Format("yyyy-MM-dd") since: LocalDate, @Format("yyyy-MM-dd") until: LocalDate, workspaceId: Long): TogglModel.TogglSummary

    @Get("https://toggl.com/reports/api/v2/details?user_agent=${userAgent}&since={since}&until={until}&workspace_id={workspaceId}&client_ids={clientId}&display_hours=minutes&page={pageNo}")
    fun entries(clientId: Long, @Format("yyyy-MM-dd") since: LocalDate, @Format("yyyy-MM-dd") until: LocalDate, pageNo: Int, workspaceId: Long): TogglModel.TogglReporting
//                                    "since" to from.format(DateTimeFormatter.ISO_DATE),
//                                    "until" to to.format(DateTimeFormatter.ISO_DATE),

    @Put("/api/v8/time_entries/{ids}")
    fun tagId(ids: String, @Body body: ObjectNode): HttpStatus

    companion object {
        const val userAgent = "https://github.com/titaniumcoder/toggl-reporting"
    }
}

object TagCreator {
    fun tagbody(billed: Boolean): ObjectNode {
        val om = ObjectMapper()
        val tags = om.createArrayNode()
        if (billed) {
            tags.add("billed")
        }

        val entry = om.createObjectNode()
        entry.set("tags", tags)

        val tagging = om.createObjectNode()
        tagging.set("time_entry", entry)

        return tagging
    }
}
