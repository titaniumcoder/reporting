package io.github.titaniumcoder.reporting.timeentry

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.github.titaniumcoder.reporting.config.Roles.Booking
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller
// TODO @RequestMapping("/api")
class TimeEntryController(val service: TimeEntryService) {
    @Secured(Booking, Admin)
    @Post("/start-timeentry")
    fun startTimeEntry(@QueryValue("ref") ref: Long?) = service.startTimeEntry(ref)

    @Secured(Booking, Admin)
    @Delete("/current-timeentry/{id}")
    fun stopTimeEntry(@PathVariable("id") id: Long) = service.stopTimeEntry(id)

    @Secured("isAuthenticated()")
    @Get("/timeentries")
    fun retrieveTimeEntries(
            @QueryValue("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @QueryValue("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @JsonFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
            @QueryValue("clientId") clientId: String?,
            @QueryValue("allEntries", defaultValue = "false") allEntries: Boolean
    ) = service.retrieveTimeEntries(from, to, clientId, allEntries, false)

    @Secured(Booking, Admin)
    @Post("/timeentries")
    fun updateTimeEntry(@Body timeentry: TimeEntryUpdateDto) = service.updateTimeEntry(timeentry)

    @Secured(Booking, Admin)
    @Delete("/timeentries/{id}")
    fun deleteTimeEntry(@PathVariable id: Long) = service.deleteTimeEntry(id)

    @Secured(Booking, Admin)
    @Post("/toggl-timeentries")
    fun togglTimeEntries(@Body ids: List<Long>) = service.togglTimeEntries(ids)
}
