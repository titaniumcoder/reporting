package io.github.titaniumcoder.reporting.timeentry

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.sse.Event
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Controller("/sse")
class TimeEntrySseController(
        val service: TimeEntryService
        // val tokenServices: ReactiveAuthenticationManager
) {
    // FIXME implement this completely, by reacting to evets for the current user
    @ExecuteOn(TaskExecutors.IO)
    @Get("current-timeentry", produces = [MediaType.TEXT_EVENT_STREAM])
    fun currentTimeEntry(): Flow<Event<TimeEntryDto?>> =
            flow {
                Event.of(
                        service.activeTimeEntry("rm") // FIXME use auth
                )
            }
    /*
    return tokenServices.authenticate(UsernamePasswordAuthenticationToken(token, token))
            .filter { it.isAuthenticated && checkBooking(it) }
            .map { it.principal as String }
            .flatMapMany {p ->
                Flux.interval(Duration.ofSeconds(0), Duration.ofSeconds(30))
                        .flatMap { service.activeTimeEntry(p) }
            }
     */

    /*
    private fun checkBooking(auth: Authentication): Boolean =
            auth
                    .authorities
                    .map { it.authority }
                    .contains(Booking)
     */
}