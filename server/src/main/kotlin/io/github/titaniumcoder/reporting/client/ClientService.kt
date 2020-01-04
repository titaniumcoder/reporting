package io.github.titaniumcoder.reporting.client

import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
@Transactional
class ClientService(val repository: ClientRepository, val userService: UserService) {
    fun clients(): List<Client> =
            repository.findAll(Sort.by("name"))

    fun saveClient(client: Client): Client {
        return repository.save(client)
    }

    fun deleteClient(id: String) {
        repository.deleteById(id);
    }

    fun clientList(): List<ClientListDto> {
        val user = userService.currentUser() ?: throw IllegalArgumentException("User is not logged in")

        val clients = repository.findActives().map { ClientListDto(it.id, it.name) }

        return if (user.admin) {
            clients
        } else {
            val userClients = user.clients.map { it.id }
            clients.filter { userClients.contains(it.id) }
        }
    }
}
