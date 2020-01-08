package io.github.titaniumcoder.reporting.reporting

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ReportingController(val service: ReportingService) {
    @GetMapping("/info")
    @Secured("isAuthenticated()")
    fun info(@RequestParam("client", required = false)clientId: String?) =
            service.info(clientId)
    /*
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
     */
}
