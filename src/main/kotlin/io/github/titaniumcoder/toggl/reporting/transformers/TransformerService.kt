package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TransformerService {
    fun cash(from: List<TogglModel.TogglSummary>): List<ViewModel.Cashout> = TODO()
    fun transformInput(input: TogglModel.TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): ViewModel.ReportingModel = TODO()
}