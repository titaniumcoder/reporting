package io.github.titaniumcoder.toggl.reporting.transformers

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class CashoutController(val client: TogglClient, val transformer: TransformerService) {
    @GetMapping("/api/cash")
    fun cash(@JsonFormat(pattern = "yyyy-MM-dd") from: LocalDate?, @JsonFormat(pattern = "yyyy-MM-dd") to: LocalDate?): List<ViewModel.Cashout> {
        val finalFrom = from ?: LocalDate.now().withDayOfYear(1)
        val finalTo = to ?: finalFrom.plusYears(1).withDayOfYear(1).minusDays(1)

        return transformer.cash(client.summary(finalFrom, finalTo))
    }
}
