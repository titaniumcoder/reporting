package io.github.titaniumcoder.reporting.client

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ClientController(val service: ClientService) {
    @Secured("isAuthenticated()")
    @GetMapping("/client-list")
    fun clientList() = service.clientList()

    @Secured("ROLE_ADMIN")
    @GetMapping("/clients")
    fun clients() = service.clients()

    @Secured("ROLE_ADMIN")
    @PostMapping("/clients")
    fun save(@RequestBody @Validated client: Client): Client {
        return service.saveClient(client)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/clients/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Unit> {
        service.deleteClient(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
