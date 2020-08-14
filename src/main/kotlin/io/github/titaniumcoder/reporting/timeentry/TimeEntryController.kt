package io.github.titaniumcoder.reporting.timeentry

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import java.time.LocalDate

@Controller("/api")
class TimeEntryController(
        private val service: TimeEntryService
) {
    @Secured("isAuthenticated()")
    @Post("/start-timeentry")
    fun startTimeEntry(@QueryValue("ref") ref: Long?) = service.startTimeEntry(ref)

    @Secured("isAuthenticated()")
    @Delete("/current-timeentry/{id}")
    fun stopTimeEntry(@PathVariable("id") id: Long) = service.stopTimeEntry(id)

    @Secured("isAuthenticated()")
    @Get("/timeentries")
    fun retrieveTimeEntries(
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate?,
            @QueryValue("to") @Format("yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
            @QueryValue("clientId") clientId: String?,
            @QueryValue("allEntries", defaultValue = "false") allEntries: Boolean
    ) = service.retrieveTimeEntries(from, to, clientId, allEntries, false)

    @Secured("isAuthenticated()")
    @Post("/timeentries")
    fun updateTimeEntry(@Body timeentry: TimeEntryUpdateDto) = service.updateTimeEntry(timeentry)

    @Secured("isAuthenticated()")
    @Delete("/timeentries/{id}")
    fun deleteTimeEntry(@PathVariable id: Long) = service.deleteTimeEntry(id)

    @Secured("isAuthenticated()")
    @Post("/toggl-timeentries")
    fun togglTimeEntries(@Body ids: List<Long>) = service.togglTimeEntries(ids)
}
