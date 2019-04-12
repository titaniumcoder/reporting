package io.github.titaniumcoder.toggl.reporting.toggl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TogglClient(val webClient: TogglWebClient) {
    private val log = LoggerFactory.getLogger(TogglClient::class.java)

    suspend fun clients(): List<TogglModel.Client> = webClient.clients()

    suspend fun summary(from: LocalDate, to: LocalDate): TogglModel.TogglSummary = webClient.summary(from, to)

    suspend fun entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean): TogglModel.TogglReporting {
        val firstPage = webClient.entries(clientId, from, to, nonBilledOnly, 1)
        val range = 2.rangeTo(
                Math.ceil(firstPage.totalCount.toDouble() / firstPage.perPage).toInt()
        )
                .map { webClient.entries(clientId, from, to, nonBilledOnly, it) }

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
                .filter { x -> !nonBilledOnly || !x.tags.contains("billed") }
                .sortedBy { x -> ClientSort(x.client ?: "", x.start.toLocalDateTime()) }
        )
    }

    suspend fun tagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        tagRange(clientId, from, to, true)
    }

    suspend fun untagBilled(clientId: Long, from: LocalDate, to: LocalDate) {
        tagRange(clientId, from, to, false)
    }

    suspend fun tagBilled(entryId: Long) {
        webClient.tagId(listOf(entryId.toString()), true)
    }

    suspend fun untagBilled(entryId: Long) {
        webClient.tagId(listOf(entryId.toString()), false)
    }

    private suspend fun tagRange(clientId: Long, from: LocalDate, to: LocalDate, billed: Boolean) {
        val entriesMatched = entries(
                clientId = clientId,
                from = from,
                to = to,
                nonBilledOnly = false
        )

        val ids = entriesMatched.data.map { it.id.toString()}.sorted().chunked(50)

        val completeResult = ids.map { webClient.tagId(it, billed)}
        if (completeResult.any { it.isError }) {
            // TODO decide what we do here for the future
            log.warn("Could not update the ids, got an error from the Toggl API")
        }
    }
}
