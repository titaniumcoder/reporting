package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class TransformerService {
    fun cash(summary: Mono<TogglModel.TogglSummary>): Flux<ViewModel.Cashout> =
            summary.map {
                it.data.map { x ->
                    ViewModel.Cashout(x.title?.name ?: "unbekannt", x.totalCurrencies.map { c ->
                        c.amount ?: 0.0
                    }.sum())
                }
            }
                    .flatMapMany { Flux.fromIterable(it) }

    fun transformInput(input: TogglModel.TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): ViewModel.ReportingModel =
            ViewModel.ReportingModel(
                    client = input.data.firstOrNull()?.client ?: "unbekannt",
                    clientId = clientId,
                    from = from,
                    to = to,
                    projects = input.data.groupBy {
                        it.project ?: "???"
                    }.map { ViewModel.Project(it.key, (it.value.map { reporting -> reporting.duration }.sum() / 60000)) },
                    timeEntries = input.data.groupBy { it.start.toLocalDate() }.map {
                        it.value.map { v ->
                            ViewModel.TimeEntry(
                                    id = v.id,
                                    day = v.start.toLocalDate(),
                                    project = v.project,
                                    startdate = v.start.toLocalDateTime(),
                                    enddate = v.end.toLocalDateTime(),
                                    minutes = v.duration / 60000,
                                    description = v.description,
                                    tags = v.tags
                            )
                        }
                    }
            )
}