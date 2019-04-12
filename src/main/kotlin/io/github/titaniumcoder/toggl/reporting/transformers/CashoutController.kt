package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class CashoutController(val client: TogglClient, val transformer: TransformerService) {
    @GetMapping("/api/cash")
    fun cash(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ) = GlobalScope.mono {
        // FIXME this can be replaced with "suspend" with Spring 5.2
        val finalFrom = from ?: LocalDate.now().withDayOfYear(1)
        val finalTo = to ?: finalFrom.plusYears(1).withDayOfYear(1).minusDays(1)

        val summary = client.summary(finalFrom, finalTo)

        transformer.cash(summary)
    }
}
