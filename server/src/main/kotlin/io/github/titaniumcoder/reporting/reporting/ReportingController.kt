package io.github.titaniumcoder.reporting.reporting

import io.micronaut.core.convert.format.Format
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.server.types.files.StreamedFile
import io.micronaut.security.annotation.Secured
import java.io.ByteArrayInputStream
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
    @Get("/timesheet/{clientId}")
    fun timesheet(
            @PathVariable("clientId") clientId: Long,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ): StreamedFile {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name.toUpperCase()}-${sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))}.xlsx"

        val input = ByteArrayInputStream(sheet.excel)

        return StreamedFile(input, MediaType.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).attach(filename)
    }
}
