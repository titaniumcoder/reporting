package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class ClientService(
        private val repository: ClientRepository
) {
    fun clients(): Flow<Client> = repository.findAllSortedById()

    suspend fun saveClient(client: Client): Client = repository.save(client)

    suspend fun deleteClient(id: String) = repository.deleteById(id)

    suspend fun clientList(): Flow<ClientListDto> =
         repository.findActives()
                .map {
                    ClientListDto(it.id, it.name)
                }

    suspend fun findById(clientId: String) = repository.findById(clientId)
}

