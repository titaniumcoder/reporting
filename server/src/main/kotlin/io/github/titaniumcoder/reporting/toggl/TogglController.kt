package io.github.titaniumcoder.reporting.toggl

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured

@Controller("/api")
class TogglController(val service: TogglService) {
    @Secured("isAuthenticated()")
    @Put("/tag/{entry}")
    fun tagEntry(@PathVariable("entry") entry: Long) =
            service.tagBilled(entry)

    @Secured("isAuthenticated()")
    @Delete("/tag/{entry}")
    fun untagEntry(@PathVariable("entry") entry: Long) =
            service.untagBilled(entry)
}
