package io.github.titaniumcoder.reporting.timeentry

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.*
import java.time.LocalDate

@Controller("/api")
class TimeEntryController(
        private val service: TimeEntryService
) {
    @Post("/start-timeentry")
    suspend fun startTimeEntry(@QueryValue("ref") ref: String?) = service.startTimeEntry(ref)

    @Delete("/current-timeentry/{id}")
    suspend fun stopTimeEntry(@PathVariable("id") id: String) = service.stopTimeEntry(id)

    @Get("/timeentries")
    suspend fun retrieveTimeEntries(
            @QueryValue("from") @Format("yyyy-MM-dd") from: LocalDate?,
            @QueryValue("to") @Format("yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
            @QueryValue("clientId") clientId: String?,
            @QueryValue("allEntries", defaultValue = "false") allEntries: Boolean
    ) = service.retrieveTimeEntries(from, to, clientId, allEntries, false)

    @Post("/timeentries")
    suspend fun updateTimeEntry(@Body timeentry: TimeEntryUpdateDto) = service.updateTimeEntry(timeentry)

    @Delete("/timeentries/{id}")
    fun deleteTimeEntry(@PathVariable id: String) = service.deleteTimeEntry(id)

    @Post("/toggl-timeentries")
    fun togglTimeEntries(@Body ids: List<String>) = service.togglTimeEntries(ids)
}
