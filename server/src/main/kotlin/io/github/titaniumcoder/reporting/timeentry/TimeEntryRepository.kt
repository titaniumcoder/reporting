package io.github.titaniumcoder.reporting.timeentry

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import java.time.LocalDate

interface TimeEntryRepository {
    @SqlQuery("select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billed from time_entry t where t.email = :email and t.ending is null order by t.starting desc nulls first limit 1")
    fun findLastOpenEntry(email: String): TimeEntry?

    @SqlQuery("""select t.id, t.starting, t.ending, t.project_id, t.description, t.email, t.billed 
            from time_entry t 
            where (:from is null or t.starting >= :from) 
            and (:to is null or t.ending < :to) 
            and (:clientId is null or t.project_id in (select p.id from project p where p.client_id = :clientId)) 
            and (:email is null or t.email = :email or t.project_id in (select p1.id from project p1 join client c1 on c1.id = p1.client_id join client_user cu on cu.client_id = c1.id where cu.email = :email)) 
            order by t.starting """)
    fun findAllWithin(@Bind("from") from: LocalDate?,
                      @Bind("to") to: LocalDate?,
                      @Bind("clientId") clientId: String?,
                      @Bind("email") email: String?): List<TimeEntry>

    @SqlQuery("""
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
            order by t.starting """)
    fun findNonBilled(@Bind("clientId") clientId: String?, @Bind("billableOnly") billableOnly: Boolean, @Bind("email") email: String?): List<TimeEntry>

    fun delete(timeentry: TimeEntry)

    fun findAllById(ids: List<Long>): List<TimeEntry>

    fun save(it: TimeEntry): TimeEntry

    fun findById(id: Long): TimeEntry?
}
