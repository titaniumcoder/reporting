package io.github.titaniumcoder.toggl.reporting.reporting

import io.micronaut.http.HttpHeaders
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.security.annotation.Secured
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller("/api")
class ReportingController(val service: ReportingService) {
    @Secured("isAuthenticated()")
    @Get("/client/{clientId}")
    suspend fun entries(
            @PathVariable("clientId") clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate?,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    @Secured("isAuthenticated()")
    @Get("/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    suspend fun timesheet(
            @PathVariable("clientId") clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ): ByteArray {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name.toUpperCase()}-${sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))}.xlsx"

        TODO()
        /*
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                    .body(sheet.excel)
         */
    }
}
