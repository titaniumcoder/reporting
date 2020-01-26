package io.github.titaniumcoder.reporting.reporting

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class ReportingController(val service: ReportingService) {
    @GetMapping("/client-info")
    @Secured("isAuthenticated()")
    fun info(@RequestParam("clientId", required = false) clientId: String?,
             @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
             @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate?) = service.info(clientId, from, to)

    @Secured("isAuthenticated()")
    @GetMapping("/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun timesheet(
            @PathVariable("clientId") clientId: String,
            @RequestParam("billableOnly") billableOnly: Boolean
    ): Mono<ResponseEntity<ByteArray>> =
            service.timesheet(clientId, billableOnly)
                    .map { sheet ->
                        val filename = "${sheet.name.toUpperCase()}.xlsx"

                        ResponseEntity
                                .ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = $filename")
                                .header("filename", filename)
                                .body(sheet.excel)
                    }
                    .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()))

}
