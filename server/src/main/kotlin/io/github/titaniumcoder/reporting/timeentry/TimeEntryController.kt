package io.github.titaniumcoder.reporting.timeentry

import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TimeEntryController(val service: TimeEntryService) {
    @Secured("ROLE_BOOKING")
    @GetMapping("/current-timeentry")
    fun currentTimeEntry(): ResponseEntity<TimeEntryDto> {
        val te = service.activeTimeEntry()
        return if (te == null) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(te)
        }
    }

    @Secured("ROLE_BOOKING")
    @PostMapping("/start-timeentry")
    fun startTimeEntry(@RequestParam("ref", required = false) ref: Long?): TimeEntryDto {
        return service.startTimeEntry(ref)
    }

    @Secured("ROLE_BOOKING")
    @PostMapping("/stop-timeentry")
    fun stopTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto): TimeEntryDto {
        return service.stopTimeEntry(timeentry);
    }

    @Secured("ROLE_BOOKING")
    @PostMapping("/timeentries")
    fun updateTimeEntry(@RequestBody timeentry: TimeEntryUpdateDto): TimeEntryDto {
        return service.updateTimeEntry(timeentry);
    }
}
