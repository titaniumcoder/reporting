package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.config.Roles
import io.github.titaniumcoder.reporting.config.Roles.Booking
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TimeEntryController(val service: TimeEntryService) {
    @Secured(Booking)
    @PostMapping("/start-timeentry")
    fun startTimeEntry(@RequestParam("ref", required = false) ref: Long?): TimeEntryDto {
        return service.startTimeEntry(ref)
    }

    @Secured(Booking)
    @PostMapping("/stop-timeentry")
    fun stopTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto): TimeEntryDto {
        return service.stopTimeEntry(timeentry);
    }

    @Secured(Booking)
    @PostMapping("/timeentries")
    fun updateTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto): TimeEntryDto {
        return service.updateTimeEntry(timeentry);
    }
}
