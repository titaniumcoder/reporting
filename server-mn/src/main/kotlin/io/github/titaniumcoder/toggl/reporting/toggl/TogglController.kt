package io.github.titaniumcoder.toggl.reporting.toggl

import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class TogglController(val service: TogglService) {
    @Secured("isAuthenticated()")
    @Get("/clients")
    suspend fun clients() = service.clients()

    @Secured("isAuthenticated()")
    @Put("/client/{clientId}/billed")
    suspend fun tagBilled(
            @PathVariable("clientId") clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ) =
            service.tagBilled(clientId, from, to)

    @Secured("isAuthenticated()")
    @Delete("/client/{clientId}/billed")
    suspend fun untagBilled(
            @PathVariable("clientId") clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ) =
            service.untagBilled(clientId, from, to)

    @Secured("isAuthenticated()")
    @Put("/tag/{entry}")
    suspend fun tagEntry(@PathVariable("entry") entry: Long) =
            service.tagBilled(entry)

    @Secured("isAuthenticated()")
    @Delete("/tag/{entry}")
    suspend fun untagEntry(@PathVariable("entry") entry: Long) =
            service.untagBilled(entry)
}
