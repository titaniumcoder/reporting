package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.LocalDate

@RestController
class CashoutController(val client: TogglClient, val transformer: TransformerService) {
    @GetMapping("/api/cash")
    fun cash(@RequestParam from: LocalDate?, @RequestParam to: LocalDate?): Flux<ViewModel.Cashout> {
        val finalFrom = from ?: LocalDate.now().withDayOfYear(1)
        val finalTo = to ?: finalFrom.plusYears(1).withDayOfYear(1).minusDays(1)

        return transformer.cash(client.summary(finalFrom, finalTo))
    }
}
