package io.github.titaniumcoder.reporting.client

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class ClientController(val service: ClientService) {
    @Secured("isAuthenticated()")
    @GetMapping("/clients")
    fun clients() = service.clients()

    @GetMapping("/client/{id}")
    fun client(@PathVariable("id") id: Int) =
            service.client(id)

    @Secured("isAuthenticated()")
    @PutMapping("/client/{clientId}/billed")
    fun tagBilled(
            @PathVariable("clientId") clientId: Int,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate
    ) =
            service.flagBilling(clientId, from, to, true)

    @Secured("isAuthenticated()")
    @DeleteMapping("/client/{clientId}/billed")
    fun untagBilled(
            @PathVariable("clientId") clientId: Int,
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") to: LocalDate
    ) =
            service.flagBilling(clientId, from, to, false)
}
