package io.github.titaniumcoder.toggl.reporting.reporting

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class ReportingController(val service: ReportingService) {
    @GetMapping("/client/{clientId}")
    @Secured("isAuthenticated()")
    suspend fun entries(
            @PathVariable clientId: Long,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") from: LocalDate?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    @GetMapping(
            value = ["/timesheet/{clientId}"],
            produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
    )
    @Secured("isAuthenticated()")
    suspend fun timesheet(
            @PathVariable clientId: Long,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") from: LocalDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") to: LocalDate
    ): ResponseEntity<ByteArray> {
        val sheet = service.timesheet(clientId, from, to)

        val filename = "${sheet.name}-${sheet.date.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx"

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                .body(sheet.excel)
    }
}
