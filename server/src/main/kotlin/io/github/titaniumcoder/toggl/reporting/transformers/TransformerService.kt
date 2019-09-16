package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@Singleton
class TransformerService {
    fun cash(summary: TogglModel.TogglSummary): List<ViewModel.Cashout> =
            summary.data.map { x ->
                ViewModel.Cashout(x.title?.name ?: "unbekannt", x.totalCurrencies.map { c ->
                    c.amount ?: 0.0
                }.sum())
            }.sortedBy { it.client }

    fun transformInput(input: TogglModel.TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): ViewModel.ReportingModel =
            ViewModel.ReportingModel(
                    client = input.data.firstOrNull()?.client ?: "unbekannt",
                    clientId = clientId,
                    from = from,
                    to = to,
                    projects = input.data.groupBy {
                        it.project ?: "???"
                    }
                            .map { ViewModel.Project(it.key, (it.value.map { reporting -> reporting.duration }.sum() / 60000)) }
                            .sortedBy { it.name },
                    timeEntries = input.data
                            .groupBy { it.start.toLocalDate() }
                            .map { entry ->
                                entry.value.map { v ->
                                    ViewModel.TimeEntry(
                                            id = v.id,
                                            day = v.start.toLocalDate(),
                                            project = v.project,
                                            startdate = v.start.truncatedTo(ChronoUnit.MINUTES),
                                            enddate = v.end.truncatedTo(ChronoUnit.MINUTES),
                                            minutes = v.duration / 60000,
                                            description = v.description,
                                            tags = v.tags
                                    )
                                }.sortedBy { it.startdate }
                            }.sortedBy { it.firstOrNull()?.day ?: LocalDate.now() }
            )
}
