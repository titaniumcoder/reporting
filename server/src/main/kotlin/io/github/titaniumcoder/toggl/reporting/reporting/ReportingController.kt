package io.github.titaniumcoder.toggl.reporting.reporting

import io.micronaut.core.convert.format.Format
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller("/api")
class ReportingController(val service: ReportingService) {
    @Secured("isAuthenticated()")
    @Get("/client/{clientId}")
    fun entries(
            @PathVariable("clientId") clientId: Long,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate?,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    @Secured("isAuthenticated()")
    @Get("/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun timesheet(
            @PathVariable("clientId") clientId: Long,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ): HttpResponse<ByteArray> {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name.toUpperCase()}-${sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))}.xlsx"

        return HttpResponse
                .ok(sheet.excel)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
    }
}
