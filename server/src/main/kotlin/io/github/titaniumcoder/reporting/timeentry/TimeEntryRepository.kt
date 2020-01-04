package io.github.titaniumcoder.reporting.timeentry

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimeEntryRepository : JpaRepository<TimeEntry, Long> {
    @Query("select t from TimeEntry t where t.user.email = :email and t.ending is null order by t.starting desc nulls first")
    fun findLastOpenEntry(email: String, pageable: Pageable): List<TimeEntry>
}
