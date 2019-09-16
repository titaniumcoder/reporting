package io.github.titaniumcoder.toggl.reporting.reporting

import io.micronaut.core.convert.format.Format
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller("/api")
class ReportingController(val service: ReportingService) {
    @Get("/client/{clientId}")
    @Secured("isAuthenticated()")
    fun entries(
            @PathVariable clientId: Long,
            @Format("yyyy-MM-dd") @QueryValue("from") from: LocalDate?,
            @Format("yyyy-MM-dd") @QueryValue("to") to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    @Get("/timesheet/{clientId}")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Secured("isAuthenticated()")
    fun timesheet(
            @PathVariable clientId: Long,
            @Format("yyyy-MM-dd") @QueryValue("from") from: LocalDate,
            @Format("yyyy-MM-dd") @QueryValue("to") to: LocalDate
    ): MutableHttpResponse<ByteArray>? {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name}-${sheet.date.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx"

        return HttpResponse
                .ok(sheet.excel)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
    }
}
