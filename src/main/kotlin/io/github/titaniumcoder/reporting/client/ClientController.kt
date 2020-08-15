package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.model.Client
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated

@Controller("/api")
class ClientController(
        private val service: ClientService
) {
    @Get("/client-list")
    suspend fun clientList() = service.clientList()

    @Get("/clients")
    fun clients() = service.clients()

    @Post("/clients")
    @Validated
    suspend fun save(@Body client: Client) = service.saveClient(client)

    @Delete("/clients/{id}")
    suspend fun delete(@PathVariable("id") id: String): HttpResponse<Unit> =
            if (service.deleteClient(id))
                HttpResponse.noContent()
            else
                HttpResponse.notModified()
}
