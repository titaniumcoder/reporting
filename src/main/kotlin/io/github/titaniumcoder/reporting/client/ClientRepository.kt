package io.github.titaniumcoder.reporting.client

import javax.inject.Singleton

@Singleton
class ClientRepository {
    fun findActives(): List<Client> = TODO()

    fun findAllSortedById(): List<Client> = TODO()

    fun findAllForUser(email: String): List<Client> = TODO()

    fun currentIdsForUser(email: String): List<String> = TODO()

    fun existsById(clientId: String): Int? = TODO()

    // TODO how to do this with JDBI?
    fun save(client: Client): Client = TODO()

    fun deleteById(id: String): Unit = TODO()

    // TODO mapping
    fun findById(clientId: String): Client? = TODO()
}
