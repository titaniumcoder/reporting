package io.github.titaniumcoder.reporting.timeentry

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface TimeEntryRepository : ReactiveCrudRepository<TimeEntry, Long> {
    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billed from time_entry t where t.email = :email and t.ending is null order by t.starting desc nulls first limit 1")
    fun findLastOpenEntry(email: String): Mono<TimeEntry>

    @Query("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billed " +
            "from time_entry t " +
            "where (:from is null or t.starting >= :from) " +
            "and (:to is null or t.ending < :to) " +
            "and (:clientId is null or t.project_id in (select p.id from project p where p.client_id = :clientId)) " +
            "and (:email is null or t.email = :email or t.project_id in (select p1.id from project p1 join client c1 on c1.id = p1.client_id join client_user cu on cu.client_id = c1.id where cu.email = :email)) " +
            "order by t.starting asc")
    fun findAllWithin(@Param("from") from: LocalDate?,
                      @Param("to") to: LocalDate?,
                      @Param("clientId") clientId: String?,
                      @Param("email") email: String?): Flux<TimeEntry>

    @Query("""
            select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billed 
            from time_entry t 
            where (
                :clientId is null or 
                t.project_id in (
                    select p.id 
                    from project p 
                    where 
                        p.client_id = :clientId and
                        (:billableOnly = false or p.billable = true)
                )
            ) and (
                :email is null or 
                t.email = :email or 
                t.project_id in (
                    select p1.id 
                    from project p1 
                        join client c1 on c1.id = p1.client_id 
                        join client_user cu on cu.client_id = c1.id 
                    where cu.email = :email
                )
            ) and (t.billed = false) 
            order by t.starting asc""")
    fun findNonBilled(@Param("clientId") clientId: String?, @Param("billableOnly") billableOnly: Boolean, @Param("email") email: String?): Flux<TimeEntry>
}
