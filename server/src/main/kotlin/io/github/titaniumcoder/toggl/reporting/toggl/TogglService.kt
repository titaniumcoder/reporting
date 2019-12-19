package io.github.titaniumcoder.toggl.reporting.toggl

import io.github.titaniumcoder.toggl.reporting.config.TogglConfiguration
import io.github.titaniumcoder.toggl.reporting.toggl.TagCreator.tagbody
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import javax.inject.Singleton
import kotlin.math.ceil

@Singleton
class TogglService(private val webClient: TogglWebClient, private val config: TogglConfiguration) {
    private val log = LoggerFactory.getLogger(TogglService::class.java)

    fun clients(): List<TogglModel.Client> = webClient.clients()

    fun summary(from: LocalDate, to: LocalDate, year: Int, doCalc: Boolean): TogglModel.TogglSummary {
        if (doCalc) {
            log.info("Need to do the whole calculation for the full year first")
            val calculatedSummary = webClient.summary(LocalDate.of(year, Month.JANUARY, 1), LocalDate.of(year, Month.DECEMBER, 31), config.workspaceId)
            updateSummary(calculatedSummary)
        }
        val summary = webClient.summary(from, to, config.workspaceId)
        return summary.copy(data = summary.data.filter { it.time > 0 })
    }

    private fun updateSummary(calculatedSummary: TogglModel.TogglSummary) {
        log.info("Entered updateSummary with {}", calculatedSummary)
        TODO("update the DB for all clients and projects according to this summary")
    }

    fun entries(clientId: Long, from: LocalDate, to: LocalDate): TogglModel.TogglReporting {
        val firstPage = webClient.entries(clientId, from, to, 1, config.workspaceId)
        val range = 2.rangeTo(
                ceil(firstPage.totalCount.toDouble() / firstPage.perPage).toInt()
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
        if (!completeResult.all { it.code == 200 }) {
            log.warn("Could not update the ids, got an error from the Toggl API")
        }
    }
}
