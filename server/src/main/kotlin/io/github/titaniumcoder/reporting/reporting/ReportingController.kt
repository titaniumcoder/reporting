package io.github.titaniumcoder.reporting.reporting

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
    @Get("/client-info")
    @Secured("isAuthenticated()")
    fun info(@QueryValue("clientId") clientId: String?,
             @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate?,
             @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate?) = service.info(clientId, from, to)

    @Secured("isAuthenticated()")
    @Get("/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun timesheet(
            @PathVariable("clientId") clientId: String,
            @QueryValue("billableOnly") billableOnly: Boolean
    ): HttpResponse<ByteArray> {
        return service.timesheet(clientId, billableOnly)
                ?.let { sheet ->
                    val dateFormatted = sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
                    val filename = "${sheet.name.toUpperCase()}-$dateFormatted.xlsx"

                    HttpResponse
                            .ok(sheet.excel)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = $filename")
                            .header("filename", filename)
                } ?: HttpResponse.noContent()
    }

}
