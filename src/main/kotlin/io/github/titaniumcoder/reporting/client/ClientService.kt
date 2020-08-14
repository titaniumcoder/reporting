package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.user.UserService
import javax.inject.Singleton

@Singleton
// TODO @Transactional
class ClientService(
        private val repository: ClientRepository,
        private val userService: UserService
) {
    fun clients() = repository.findAllSortedById()

    fun saveClient(clientDto: ClientUpdatingDto): Client {
        val exists = repository.existsById(clientDto.clientId) != null

        return repository.save(
                Client(
                        clientId = clientDto.clientId,
                        active = clientDto.active,
                        maxMinutes = clientDto.maxMinutes,
                        name = clientDto.name,
                        newClient = !exists,
                        notes = clientDto.notes,
                        rateInCentsPerHour = clientDto.rateInCentsPerHour
                )
        )
    }

    fun deleteClient(id: String) = repository.deleteById(id)

    fun clientList(): List<ClientListDto> {
        val user = userService.reactiveCurrentUserDto()

        val clients = repository.findActives().map { ClientListDto(it.clientId, it.name) }

        return user
                ?.let { u ->
                    if (u.admin) {
                        clients
                    } else {
                        clients
                                .filter { u.clients.map { c -> c.clientId }.contains(it.clientId) }
                    }
                } ?: listOf()
    }

    fun findById(clientId: String) = repository.findById(clientId)
}

