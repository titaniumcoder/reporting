package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Transactional
class ClientService(val repository: ClientRepository, val userService: UserService) {
    fun clients(): Flux<Client> = repository.findAllSortedById()

    fun saveClient(clientDto: ClientUpdatingDto): Mono<Client> {
        return repository.existsById(clientDto.clientId)
                .map {
                    Client(
                            clientId = clientDto.clientId,
                            active = clientDto.active,
                            maxMinutes = clientDto.maxMinutes,
                            name = clientDto.name,
                            newClient = !it,
                            notes = clientDto.notes,
                            rateInCentsPerHour = clientDto.rateInCentsPerHour
                    )
                }
                .flatMap { repository.save(it) }
    }

    fun deleteClient(id: String) =
            repository.deleteById(id)

    fun clientList(): Flux<ClientListDto> {
        val user = userService.reactiveCurrentUserDto()

        val clients = repository.findActives().map { ClientListDto(it.clientId, it.name) }

        return user
                .flux()
                .flatMap { u ->
                    if (u.admin) {
                        clients
                    } else {
                        clients
                                .filter { u.clients.map { c -> c.clientId }.contains(it.clientId) }
                    }
                }
    }

    fun findById(clientId: String) = repository.findById(clientId)
}

