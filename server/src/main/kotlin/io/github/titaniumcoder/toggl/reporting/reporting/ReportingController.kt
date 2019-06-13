package io.github.titaniumcoder.toggl.reporting.reporting

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class ReportingController(val service: ReportingService) {
    @GetMapping("/api/client/{clientId}")
    @PreAuthorize("isAuthenticated()")
    fun entries(
            @PathVariable clientId: Long,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?
    ) = GlobalScope.mono {
        // TODO unneeded after Spring 5.2
        service.entries(clientId, from, to)
    }

    @GetMapping("/api/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    @PreAuthorize("isAuthenticated()")
    fun timesheet(
            @PathVariable clientId: Long,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ) = GlobalScope.mono {
        val sheet = service.timesheet(clientId, from, to)

        val h = HttpHeaders()
        h.contentDisposition = ContentDisposition.builder("attachment")
                .filename("${sheet.name}-${sheet.date.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
                .size(sheet.excel.size.toLong())
                .build()

        ResponseEntity
                .ok()
                .headers(h)
                .body(sheet.excel)
    }
}
