package io.github.titaniumcoder.reporting.transformers

import io.github.titaniumcoder.reporting.toggl.TogglService
import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class HeaderinfoController(private val service: TogglService, private val transformer: TransformerService) {
    @Secured("isAuthenticated()")
    @Get("/headerinfo")
    fun cash(
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate?,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate?,
            @QueryValue("year") year: Int?,
            @QueryValue("withCalc", defaultValue = "false") withCalc: Boolean
    ): ViewModel.HeaderInfo {
        val finalFrom = from ?: LocalDate.now().minusMonths(3).withDayOfMonth(1)
        val finalTo = to ?: LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1)
        val finalYear = year ?: LocalDate.now().year

        val summary = service.summary(finalFrom, finalTo, finalYear, withCalc)

        return transformer.cash(summary)
    }
}
