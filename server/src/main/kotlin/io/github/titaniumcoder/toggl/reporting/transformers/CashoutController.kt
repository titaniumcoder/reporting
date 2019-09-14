package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class CashoutController(val service: TogglService, val transformer: TransformerService) {
    @Get("/cash")
    @Secured("isAuthenticated()")
    fun cash(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ): List<ViewModel.Cashout> {
        val finalFrom = from ?: LocalDate.now().minusMonths(3).withDayOfMonth(1)
        val finalTo = to ?: LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)

        val summary = service.summary(finalFrom, finalTo)

        return transformer.cash(summary)
    }
}
