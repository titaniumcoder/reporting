package io.github.titaniumcoder.reporting.reporting

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class ReportingController(val service: ReportingService) {
    @Secured("isAuthenticated()")
    @GetMapping("/client/{clientId}")
    fun entries(
            @PathVariable("clientId") clientId: Long,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    @Secured("isAuthenticated()")
    @GetMapping("/timesheet/{clientId}")
    fun timesheet(
            @PathVariable("clientId") clientId: Long,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate
    ): ResponseEntity<ByteArray> {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name.toUpperCase()}-${sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))}.xlsx"

        val input = ByteArrayInputStream(sheet.excel)

        TODO("create response entity")

        // return StreamedFile(input, MediaType.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).attach(filename)
    }
}
