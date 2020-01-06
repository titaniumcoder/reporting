package io.github.titaniumcoder.reporting.project

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ProjectRepository : ReactiveCrudRepository<Project, Long> {
    @Query("select p.id, p.client_id, p.active, p.name, p.max_minutes, p.rate_in_cents_per_hour, p.billable from project p order by p.name")
    fun findAllSortedByName(): Flux<Project>
}
