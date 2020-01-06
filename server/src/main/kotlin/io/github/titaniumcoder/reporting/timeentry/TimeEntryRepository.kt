package io.github.titaniumcoder.reporting.timeentry

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface TimeEntryRepository : ReactiveCrudRepository<TimeEntry, Long> {
    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billable, t.billed from time_entry t where t.email = :email and t.ending is null order by t.starting desc nulls first limit 1")
    fun findLastOpenEntry(email: String): Mono<TimeEntry>
}
