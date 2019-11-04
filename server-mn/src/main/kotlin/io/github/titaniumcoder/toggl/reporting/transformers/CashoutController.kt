package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import java.time.LocalDate

//@RestController
//@RequestMapping("/api")
class CashoutController(val service: TogglService, val transformer: TransformerService) {
//    @GetMapping("/cash")
//    @Secured("isAuthenticated()")
    suspend fun cash(
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate?,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate?
    ): List<ViewModel.Cashout> {
        val finalFrom = from ?: LocalDate.now().minusMonths(3).withDayOfMonth(1)
        val finalTo = to ?: LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)

        val summary = service.summary(finalFrom, finalTo)

        return transformer.cash(summary)
    }
}
