package io.github.titaniumcoder.toggl.reporting.toggl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.micronaut.core.convert.format.Format
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.reactivestreams.Publisher
import java.time.LocalDate

@Client("https://toggl.com/")
interface TogglWebClient {
    companion object {
        const val userAgent = "https://github.com/titaniumcoder/toggl-reporting"
    }

    @Get("https://www.toggl.com/api/v8/clients")
    fun clients(): List<TogglModel.Client>

    @Get("https://toggl.com/reports/api/v2/summary?user_agent={userAgent}&since={since}&until={until}&tag_ids=0&grouping=clients&subgrouping=projects&subgrouping_ids=false&workspace_id={workspaceId}")
    fun summary(
            @Format("yyyy-MM-dd") @PathVariable("since") from: LocalDate,
            @Format("yyyy-MM-dd") @PathVariable("until") to: LocalDate,
            @PathVariable("workspaceId") workspaceId: Long,
            @PathVariable("userAgent") userAgent: String = TogglWebClient.userAgent
    ): TogglModel.TogglSummary
//                                    "since" to from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),

    @Get("https://toggl.com/reports/api/v2/details?user_agent={userAgent}&since={since}&until={until}&workspace_id={workspaceId}&client_ids={clientIds}&display_hours=minutes&page={page}")
    fun entries(
            @PathVariable("clientIds") clientId: Long,
            @Format("yyyy-MM-dd") @PathVariable("since") from: LocalDate,
            @Format("yyyy-MM-dd") @PathVariable("until") to: LocalDate,
            @PathVariable("page") pageNo: Int,
            @PathVariable("workspaceId") workspaceId: Long,
            @PathVariable("userAgent") userAgent: String = TogglWebClient.userAgent
    ): TogglModel.TogglReporting

    @Put("https://www.toggl.com/api/v8/time_entries/{ids}")
    fun tagId(@PathVariable("ids") ids: String, @Body body: ObjectNode): HttpStatus
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

@Filter("/**")
class TogglFilter(private val config: TogglConfiguration) : HttpClientFilter {
    override fun doFilter(request: MutableHttpRequest<*>, chain: ClientFilterChain): Publisher<out HttpResponse<*>> =
            chain.proceed(request.basicAuth(config.apiToken, "api_token"))

}
