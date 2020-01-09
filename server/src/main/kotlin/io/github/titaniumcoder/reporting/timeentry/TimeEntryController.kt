package io.github.titaniumcoder.reporting.timeentry

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.github.titaniumcoder.reporting.config.Roles.Booking
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class TimeEntryController(val service: TimeEntryService) {
    @Secured(Booking, Admin)
    @PostMapping("/start-timeentry")
    fun startTimeEntry(@RequestParam("ref", required = false) ref: Long?) = service.startTimeEntry(ref)

    @Secured(Booking, Admin)
    @DeleteMapping("/current-timeentry/{id}")
    fun stopTimeEntry(@PathVariable("id", required = true) id: Long) = service.stopTimeEntry(id)

    @Secured("isAuthenticated()")
    @GetMapping("/timeentries")
    fun retrieveTimeEntries(
            @RequestParam("from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam("to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @JsonFormat(pattern = "yyyy-MM-dd") to: LocalDate?,
            @RequestParam("clientId", required = false) clientId: String?,
            @RequestParam("allEntries", required = false, defaultValue = "false") allEntries: Boolean
    ) = service.retrieveTimeEntries(from, to, clientId, allEntries, false)

    @Secured(Booking, Admin)
    @PostMapping("/timeentries")
    fun updateTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto) = service.updateTimeEntry(timeentry)

    @Secured(Booking, Admin)
    @DeleteMapping("/timeentries/{id}")
    fun deleteTimeEntry(@PathVariable id: Long) = service.deleteTimeEntry(id)

    @Secured(Booking, Admin)
    @PostMapping("/toggl-timeentries")
    fun togglTimeEntries(@RequestBody ids: List<Long>) = service.togglTimeEntries(ids)
}
