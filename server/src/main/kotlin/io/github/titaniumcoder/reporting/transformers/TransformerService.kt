package io.github.titaniumcoder.reporting.transformers

import io.github.titaniumcoder.reporting.toggl.TogglModel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TransformerService {
    fun cash(summary: TogglModel.TogglSummary): ViewModel.HeaderInfo {
        val cashouts = summary.data.map { x ->
            ViewModel.Cashout(
                    client = x.title?.name ?: "unbekannt",
                    amount = x.totalCurrencies.map { c -> c.amount ?: 0.0 }.sum(),
                    minutesWorked = (x.time / 1000 / 60).toInt(),
                    percentage = null,
                    minutesTotal = null
            )
        }.sortedBy { it.client }

        return ViewModel.HeaderInfo(
                cashouts = cashouts,
                totalCashout = cashouts.sumByDouble { it.amount }
        )
    }

    fun transformInput(input: TogglModel.TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): ViewModel.ReportingModel =
            ViewModel.ReportingModel(
                    client = input.data.firstOrNull()?.client ?: "unbekannt",
                    clientId = clientId,
                    from = from,
                    to = to,
                    projects = input.data.groupBy {
                        it.project ?: "???"
                    }
                            .map {
                                val totalMinutes = Random.nextInt(5000)
                                val openMinutes = Random.nextInt(3000)
                                val billedMinutes = Random.nextInt(2000)

                                ViewModel.Project(
                                        name = it.key,
                                        minutesWorked = (it.value.map { reporting -> reporting.duration }.sum() / 60000), // TODO fix this
                                        minutesTotal = totalMinutes, // TODO fix this
                                        percentage = if (totalMinutes != 0) {
                                            (billedMinutes.toDouble() + openMinutes) / totalMinutes * 100
                                        } else {
                                            null
                                        }
                                )
                            }
                            .sortedBy { it.name },
                    timeEntries = input.data
                            .groupBy { it.start.withZoneSameInstant(zone).toLocalDate() }
                            .map { entry ->
                                entry.value.map { v ->
                                    ViewModel.TimeEntry(
                                            id = v.id,
                                            day = v.start.withZoneSameInstant(zone).toOffsetDateTime().truncatedTo(ChronoUnit.DAYS),
                                            project = v.project,
                                            startdate = v.start.withZoneSameInstant(zone).truncatedTo(ChronoUnit.MINUTES).toOffsetDateTime(),
                                            enddate = v.end.withZoneSameInstant(zone).truncatedTo(ChronoUnit.MINUTES).toOffsetDateTime(),
                                            minutes = v.duration / 60000,
                                            description = v.description,
                                            tags = v.tags
                                    )
                                }.sortedBy { it.startdate }
                            }.sortedBy { it.firstOrNull()?.day ?: OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS) }
            )

    companion object {
        val zone: ZoneId = ZoneId.of("Europe/Zurich")
    }
}
