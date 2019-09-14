package io.github.titaniumcoder.toggl.reporting.toggl

import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class TogglController(val service: TogglService) {
    @Get("/clients")
    @Secured("isAuthenticated()")
    fun clients() =
        service.clients()

    @Put("/client/{clientId}/billed")
    @Secured("isAuthenticated()")
    fun tagBilled(
            @PathVariable clientId: Long,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ) {
        return service.tagBilled(clientId, from, to)
    }

    @Delete("/client/{clientId}/billed")
    @Secured("isAuthenticated()")
    fun untagBilled(
            @PathVariable clientId: Long,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ) {
        return service.untagBilled(clientId, from, to)
    }

    @Put("/tag/{entry}")
    @Secured("isAuthenticated()")
    fun tagEntry(@PathVariable entry: Long) {
        return service.tagBilled(entry)
    }

    @Delete("/tag/{entry}")
    @Secured("isAuthenticated()")
    fun untagEntry(@PathVariable entry: Long) {
        return service.untagBilled(entry)
    }
}
