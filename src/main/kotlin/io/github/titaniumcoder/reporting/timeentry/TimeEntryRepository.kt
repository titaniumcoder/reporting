package io.github.titaniumcoder.reporting.timeentry

import io.github.titaniumcoder.reporting.model.CurrentTimeEntry
import io.github.titaniumcoder.reporting.model.TimeEntry
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
class TimeEntryRepository {
    fun findCurrentEntry(): CurrentTimeEntry? = TODO()

    fun findAllWithin(from: LocalDate?,
                      to: LocalDate?,
                      clientId: String?): List<TimeEntry> = TODO()

    fun findNonBilled(clientId: String?, billableOnly: Boolean): List<TimeEntry> = TODO()

    fun delete(timeentry: TimeEntry): Unit = TODO()

    fun findAllById(ids: List<String>): List<TimeEntry> = TODO()

    fun save(it: TimeEntry): TimeEntry = TODO()

    fun save(it: CurrentTimeEntry): CurrentTimeEntry = TODO()

    fun findById(id: String): TimeEntry? = TODO()
}
