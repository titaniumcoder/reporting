package io.github.titaniumcoder.reporting.timeentry

import java.time.LocalDate
import javax.inject.Singleton

@Singleton
class TimeEntryRepository {
    fun findLastOpenEntry(email: String): TimeEntry? = TODO()

    fun findAllWithin(from: LocalDate?,
                      to: LocalDate?,
                      clientId: String?,
                      email: String?): List<TimeEntry> = TODO()

    fun findNonBilled(clientId: String?, billableOnly: Boolean, email: String?): List<TimeEntry> = TODO()

    fun delete(timeentry: TimeEntry): Unit = TODO()

    fun findAllById(ids: List<Long>): List<TimeEntry> = TODO()

    fun save(it: TimeEntry): TimeEntry = TODO()

    fun findById(id: Long): TimeEntry? = TODO()
}
