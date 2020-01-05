package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.config.Roles.Admin
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

    @Secured(Admin)
    @GetMapping("/clients")
    fun clients() = service.clients()

    @Secured(Admin)
    @PostMapping("/clients")
    fun save(@RequestBody @Validated client: Client): Client {
        return service.saveClient(client)
    }

    @Secured(Admin)
    @DeleteMapping("/clients/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<Unit> {
        service.deleteClient(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
