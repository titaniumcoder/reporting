package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.github.titaniumcoder.reporting.config.Roles.Booking
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

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
            @RequestParam("from", required = false) from: LocalDateTime?,
            @RequestParam("to", required = false) to: LocalDateTime?,
            @RequestParam("clientId", required = false) clientId: String?,
            @RequestParam("allEntries", required = false, defaultValue = "false") allEntries: Boolean
    )= service.retrieveTimeEntries(from, to, clientId, allEntries)

    @Secured(Booking, Admin)
    @PostMapping("/timeentries")
    fun updateTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto)= service.updateTimeEntry(timeentry)

    @Secured(Booking, Admin)
    @DeleteMapping("/timeentries/{id}")
    fun deleteTimeEntry(@PathVariable id: Long)= service.deleteTimeEntry(id)

    @Secured(Booking, Admin)
    @PostMapping("/toggl-timeentries")
    fun togglTimeEntries(@RequestBody ids: List<Long>)= service.togglTimeEntries(ids)
}
