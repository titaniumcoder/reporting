package io.github.titaniumcoder.reporting.project

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface ProjectRepository {
    @SqlQuery("select p.id, p.client_id, p.active, p.name, p.max_minutes, p.rate_in_cents_per_hour, p.billable from project p order by p.name")
    fun findAllSortedByName(): List<Project>

    // TODO implement this
    fun save(project: Project): Project

    // TODO implement this
    fun findById(id: Long): Project?

    @SqlQuery("delete from Project p where p.id = :id")
    fun deleteById(@Bind("id") id: Long)
}
