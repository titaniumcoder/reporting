package io.github.titaniumcoder.reporting.timeentry

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime

interface TimeEntryRepository : ReactiveCrudRepository<TimeEntry, Long> {
    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billable, t.billed from time_entry t where t.email = :email and t.ending is null order by t.starting desc nulls first limit 1")
    fun findLastOpenEntry(email: String): Mono<TimeEntry>

    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billable, t.billed " +
            "from time_entry t " +
            "where (:from is null or t.starting >= :from) " +
            "and (:to is null or t.ending < :to) " +
            "and (:clientId is null or t.project_id in (select p.id from project p where p.client_id = :clientId)) " +
            "order by t.starting asc")
    fun findAllWithin(@Param("from") from: LocalDate?,
                      @Param("to") to: LocalDate?,
                      @Param("clientId") clientId: String?): Flux<TimeEntry>

    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billable, t.billed " +
            "from time_entry t " +
            "where (:clientId is null or t.project_id in (select p.id from project p where p.client_id = :clientId)) " +
            "and (t.billed = false) " +
            "order by t.starting asc")
    fun findNonBilled(@Param("clientId") clientId: String?): Flux<TimeEntry>
}
