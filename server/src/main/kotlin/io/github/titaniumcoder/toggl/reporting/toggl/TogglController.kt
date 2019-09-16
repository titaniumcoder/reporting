package io.github.titaniumcoder.toggl.reporting.toggl

import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class TogglController(val service: TogglService) {
    @Get("/clients")
    @Secured("isAuthenticated()")
    fun clients() = service.clients()

    @Put("/client/{clientId}/billed")
    @Secured("isAuthenticated()")
    fun tagBilled(
            @PathVariable clientId: Long,
            @Format("yyyy-MM-dd") @QueryValue("from") from: LocalDate,
            @Format("yyyy-MM-dd") @QueryValue("to") to: LocalDate
    ) =
            service.tagBilled(clientId, from, to)

    @Delete("/api/client/{clientId}/billed")
    @Secured("isAuthenticated()")
    fun untagBilled(
            @PathVariable clientId: Long,
            @Format("yyyy-MM-dd") @QueryValue("from") from: LocalDate,
            @Format("yyyy-MM-dd") @QueryValue("to") to: LocalDate
    ) =
            service.untagBilled(clientId, from, to)

    @Put("/api/tag/{entry}")
    @Secured("isAuthenticated()")
    fun tagEntry(@PathVariable entry: Long) =
            service.tagBilled(entry)

    @Delete("/api/tag/{entry}")
    @Secured("isAuthenticated()")
    fun untagEntry(@PathVariable entry: Long) =
            service.untagBilled(entry)
}
