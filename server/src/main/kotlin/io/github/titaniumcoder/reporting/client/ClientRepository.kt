package io.github.titaniumcoder.reporting.client

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ClientRepository : ReactiveCrudRepository<Client, String> {
    @Query("select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c where c.active = true order by c.name")
    fun findActives(): Flux<Client>

    @Query("select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c order by c.id")
    fun findAllSortedById(): Flux<Client>;

    @Query("select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c " +
            "join Client_user cu on cu.client_id = c.id where cu.email = :email order by c.id")
    fun findAllForUser(email: String): Flux<Client>
}
