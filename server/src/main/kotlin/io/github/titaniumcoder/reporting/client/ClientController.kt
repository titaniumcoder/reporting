package io.github.titaniumcoder.reporting.client

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ClientController(val service: ClientService) {
    @Secured("isAuthenticated()")
    @GetMapping("/clients")
    fun clients() = service.clients()

    @Secured("ROLE_ADMIN")
    @PostMapping("/clients")
    fun save(@RequestBody @Validated client: Client): Client {
        return service.saveClient(client)
    }

    @GetMapping("/client/{id}")
    fun client(@PathVariable("id") id: Int) =
            service.client(id)

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/clients/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Unit> {
        service.deleteClient(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }


    /*
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
     */
}
