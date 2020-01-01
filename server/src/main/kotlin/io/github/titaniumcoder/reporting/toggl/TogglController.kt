package io.github.titaniumcoder.reporting.toggl

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TogglController(val service: TogglService) {
    @Secured("isAuthenticated()")
    @PutMapping("/tag/{entry}")
    fun tagEntry(@PathVariable("entry") entry: Long) =
            service.tagBilled(entry)

    @Secured("isAuthenticated()")
    @DeleteMapping("/tag/{entry}")
    fun untagEntry(@PathVariable("entry") entry: Long) =
            service.untagBilled(entry)
}
