package io.github.titaniumcoder.toggl.reporting.toggl

import io.micronaut.http.annotation.PathVariable
import java.time.LocalDate

//@RestController
//@RequestMapping("/api")
class TogglController(val service: TogglService) {
    //    @GetMapping("/clients")
//    @Secured("isAuthenticated()")
    suspend fun clients() = service.clients()

    //    @PutMapping("/client/{clientId}/billed")
//    @Secured("isAuthenticated()")
    suspend fun tagBilled(
            @PathVariable clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ) =
            service.tagBilled(clientId, from, to)

    //    @DeleteMapping("/client/{clientId}/billed")
//    @Secured("isAuthenticated()")
    suspend fun untagBilled(
            @PathVariable clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ) =
            service.untagBilled(clientId, from, to)

    //    @PutMapping("/tag/{entry}")
//    @Secured("isAuthenticated()")
    suspend fun tagEntry(@PathVariable entry: Long) =
            service.tagBilled(entry)

    //    @DeleteMapping("/tag/{entry}")
//    @Secured("isAuthenticated()")
    suspend fun untagEntry(@PathVariable entry: Long) =
            service.untagBilled(entry)
}
