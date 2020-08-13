package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.validation.Validated

@Controller("/api")
class ClientController(
        private val service: ClientService
) {
    @Secured("isAuthenticated()")
    @Get("/client-list")
    fun clientList() = service.clientList()

    @Secured(Admin)
    @Get("/clients")
    fun clients() = service.clients()

    @Secured(Admin)
    @Post("/clients")
    @Validated
    fun save(@Body client: ClientUpdatingDto) = service.saveClient(client)

    @Secured(Admin)
    @Delete("/clients/{id}")
    fun delete(@PathVariable("id") id: String) {
        service.deleteClient(id)
    }
}
