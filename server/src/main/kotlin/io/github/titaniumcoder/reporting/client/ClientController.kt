package io.github.titaniumcoder.reporting.client

import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class ClientController(val service: ClientService) {
    @Secured("isAuthenticated()")
    @Get("/clients")
    fun clients() = service.clients()

    @Get("/client/{id}")
    fun client(@PathVariable("id") id: Int) =
            service.client(id)

    @Secured("isAuthenticated()")
    @Put("/client/{clientId}/billed")
    fun tagBilled(
            @PathVariable("clientId") clientId: Int,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ) =
            service.flagBilling(clientId, from, to, true)

    @Secured("isAuthenticated()")
    @Delete("/client/{clientId}/billed")
    fun untagBilled(
            @PathVariable("clientId") clientId: Int,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ) =
            service.flagBilling(clientId, from, to, false)
}
