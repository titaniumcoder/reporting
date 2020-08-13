package io.github.titaniumcoder.reporting.client

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface ClientRepository {
    @SqlQuery("select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c where c.active = true order by c.name")
    fun findActives(): List<Client>

    @SqlQuery("select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c order by c.id")
    fun findAllSortedById(): List<Client>

    @SqlQuery("""select c.id, c.active, c.name, c.notes, c.max_minutes, c.rate_in_cents_per_hour from Client c 
                       join Client_user cu on cu.client_id = c.id where cu.email = :email order by c.id""")
    fun findAllForUser(@Bind("email") email: String): List<Client>

    @SqlQuery("select c.id from Client c join client_user cu on cu.client_id = c.id where cu.email = :email order by c.id")
    fun currentIdsForUser(@Bind("email") email: String): List<String>

    @SqlQuery("select 1 from Client c where c.id = :clientId")
    fun existsById(@Bind("clientId") clientId: String): Int?

    // TODO how to do this with JDBI?
    fun save(client: Client): Client

    @SqlUpdate("delete from Client c where c.id = :id")
    fun deleteById(@Bind("id") id: String)

    // TODO mapping
    fun findById(clientId: String): Client?
}
