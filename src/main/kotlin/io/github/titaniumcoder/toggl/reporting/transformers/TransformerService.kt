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

    fun transformInput(input: TogglModel.TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): ViewModel.ReportingModel = TODO()
}