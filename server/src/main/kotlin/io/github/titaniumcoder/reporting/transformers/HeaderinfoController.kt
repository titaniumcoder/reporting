package io.github.titaniumcoder.reporting.transformers

import io.github.titaniumcoder.reporting.toggl.TogglService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class HeaderinfoController(private val service: TogglService, private val transformer: TransformerService) {
    @Secured("isAuthenticated()")
    @GetMapping("/headerinfo")
    fun cash(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
            @RequestParam("year") year: Int?,
            @RequestParam("withCalc", defaultValue = "false") withCalc: Boolean
    ): ViewModel.HeaderInfo {
        val finalFrom = from ?: LocalDate.now().minusMonths(3).withDayOfMonth(1)
        val finalTo = to ?: LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)
        val finalYear = year ?: LocalDate.now().year

        val summary = service.summary(finalFrom, finalTo, finalYear, withCalc)

        return transformer.cash(summary)
    }
}
