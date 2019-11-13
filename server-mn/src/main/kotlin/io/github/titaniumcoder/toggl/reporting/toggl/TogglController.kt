package io.github.titaniumcoder.toggl.reporting.toggl

import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class TogglController(val service: TogglService) {
    @Secured("isAuthenticated()")
    @Get("/clients")
    fun clients() = service.clients()

    @Secured("isAuthenticated()")
    @Put("/client/{clientId}/billed")
    fun tagBilled(
            @PathVariable("clientId") clientId: Long,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ) =
            service.tagBilled(clientId, from, to)

    @Secured("isAuthenticated()")
    @Delete("/client/{clientId}/billed")
    fun untagBilled(
            @PathVariable("clientId") clientId: Long,
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate,
            @QueryValue("to") @Format("yyyy-MM-dd") to: LocalDate
    ) =
            service.untagBilled(clientId, from, to)

    @Secured("isAuthenticated()")
    @Put("/tag/{entry}")
    fun tagEntry(@PathVariable("entry") entry: Long) =
            service.tagBilled(entry)

    @Secured("isAuthenticated()")
    @Delete("/tag/{entry}")
    fun untagEntry(@PathVariable("entry") entry: Long) =
            service.untagBilled(entry)
}
