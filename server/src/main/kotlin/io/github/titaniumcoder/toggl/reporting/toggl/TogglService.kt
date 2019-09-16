package io.github.titaniumcoder.toggl.reporting.toggl

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.github.titaniumcoder.toggl.reporting.toggl.TagCreator.tagbody
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class TogglService(val webClient: TogglWebClient, val config: TogglConfiguration) {
    private val log = LoggerFactory.getLogger(TogglService::class.java)

    fun clients(): List<TogglModel.Client> = webClient.clients()

    fun summary(from: LocalDate, to: LocalDate): TogglModel.TogglSummary {
        val summary = webClient.summary(from, to, config.workspaceId)
        return summary.copy(data = summary.data.filter { it.time > 0 })
    }

    fun entries(clientId: Long, from: LocalDate, to: LocalDate): TogglModel.TogglReporting {
        val firstPage = webClient.entries(clientId, from, to, 1, config.workspaceId)
        val range = 2.rangeTo(
                Math.ceil(firstPage.totalCount.toDouble() / firstPage.perPage).toInt()
        )
                .map { webClient.entries(clientId, from, to, it, config.workspaceId) }

        data class ClientSort(val client: String, val start: LocalDateTime) : Comparable<ClientSort> {
            override fun compareTo(other: ClientSort): Int =
                    when {
                        client.compareTo(other.client) != 0 -> client.compareTo(other.client)
                        else -> start.compareTo(other.start)
                    }
        }

        val summary = (listOf(firstPage) + range)
                .reduce { t: TogglModel.TogglReporting, u: TogglModel.TogglReporting -> t.copy(data = t.data + u.data) }

        return summary.copy(data = summary.data
                .sortedBy { x -> ClientSort(x.client ?: "", x.start.toLocalDateTime()) }
        )
    }

    fun tagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        tagRange(clientId, from, to, true)
    }

    fun untagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        tagRange(clientId, from, to, false)
    }

    fun tagBilled(entryId: Long) {
        webClient.tagId(listOf(entryId.toString()).joinToString(","), tagbody(true))
    }

    fun untagBilled(entryId: Long) {
        webClient.tagId(listOf(entryId.toString()).joinToString(","), tagbody(false))
    }

    private fun tagRange(clientId: Long, from: LocalDate, to: LocalDate, billed: Boolean) {
        val entriesMatched = entries(
                clientId = clientId,
                from = from,
                to = to
        )

        val ids = entriesMatched.data.map { it.id.toString() }.sorted().chunked(50)

        val completeResult = ids.map { webClient.tagId(it.joinToString(","), tagbody(billed)) }
        if (completeResult.any { it.code != 200 }) {
            log.warn("Could not update the ids, got an error from the Toggl API")
        }
    }
}
