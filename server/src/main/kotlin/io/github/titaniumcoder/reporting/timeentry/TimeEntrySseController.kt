package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.config.Roles.Booking
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration

@RestController
@RequestMapping("/sse")
class TimeEntrySseController(val service: TimeEntryService, val tokenServices: ReactiveAuthenticationManager) {
    @GetMapping("/current-timeentry", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun currentTimeEntry(@RequestParam("token") token: String, response: ServerHttpResponse): Flux<TimeEntryDto> {
        response.headers.set(HttpHeaders.CACHE_CONTROL, "no-transform")
        return tokenServices.authenticate(UsernamePasswordAuthenticationToken(token, token))
                .filter { it.isAuthenticated && checkBooking(it) }
                .map { it.principal as String }
                .flatMapMany {p ->
                    Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(1))
                            .flatMap { service.activeTimeEntry(p) }
                }
    }

    private fun checkBooking(auth: Authentication): Boolean =
            auth
                    .authorities
                    .map { it.authority }
                    .contains(Booking)
}
