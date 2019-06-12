package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class CashoutController(val service: TogglService, val transformer: TransformerService) {
    @GetMapping("/api/cash")
    @PreAuthorize("isAuthenticated()")
    fun cash(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ) = GlobalScope.mono {
        // FIXME this can be replaced with "suspend" with Spring 5.2
        val finalFrom = from ?: LocalDate.now().minusMonths(3).withDayOfMonth(1)
        val finalTo = to ?: LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)

        val summary = service.summary(finalFrom, finalTo)

        transformer.cash(summary)
    }
}
