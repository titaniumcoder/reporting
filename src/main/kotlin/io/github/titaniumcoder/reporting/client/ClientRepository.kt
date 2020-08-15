package io.github.titaniumcoder.reporting.client

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections.fields
import com.mongodb.client.model.Projections.include
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.Sorts
import com.mongodb.reactivestreams.client.MongoClient
import io.github.titaniumcoder.reporting.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class ClientRepository(
        private val client: MongoClient
) {
    private fun connectToDb() =
            client.getDatabase("reporting")

    private fun connectToClients() =
            connectToDb().getCollection("clients", Client::class.java)

    private fun extendClient(client: Client): Client {
        val p = client.projects
                .map { p ->
                    val te = p.timeEntries
                            .map { te -> te.copy(projectId = p.id) }

                    p.copy(
                            clientId = client.id,
                            timeEntries = te
                    )
                }

        return Client(
                id = client.id,
                active = client.active,
                name = client.name,
                notes = client.notes,
                maxMinutes = client.maxMinutes,
                rateInCentsPerHour = client.rateInCentsPerHour,
                projects = p
        )
    }

    fun findActives(): Flow<Client> =
            connectToClients()
                    .find(eq("active", true))
                    .sort(Sorts.ascending("name"))
                    .projection(fields(include("id", "name")))
                    .asFlow()
                    .map { extendClient(it) }

    fun findAllSortedById(): Flow<Client> =
            connectToClients()
                    .find()
                    .sort(Sorts.ascending("name"))
                    .asFlow()
                    .map { extendClient(it) }

    suspend fun save(client: Client): Client {
        val result = connectToClients()
                .replaceOne(
                        eq("_id", client.id),
                        client,
                        ReplaceOptions().upsert(true))
                .awaitSingle()

        log.info("Updated Client, result is $result")

        return client
    }

    suspend fun deleteById(id: String): Boolean =
            connectToClients()
                    .deleteOne(eq("_id", id))
                    .awaitSingle()
                    .deletedCount > 0L

    suspend fun findById(clientId: String): Client? =
            connectToClients()
                    .find(eq("_id", clientId))
                    .awaitFirstOrNull()

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ClientRepository::class.java)
    }
}
