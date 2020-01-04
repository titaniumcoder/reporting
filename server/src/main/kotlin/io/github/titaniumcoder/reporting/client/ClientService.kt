package io.github.titaniumcoder.reporting.client

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ClientService(val repository: ClientRepository) {
    fun clients(): List<Client> =
            repository.findAll(Sort.by("name"))

    fun saveClient(client: Client): Client {
        return repository.save(client)
    }

    fun deleteClient(id: String) {
        repository.deleteById(id);
    }

    fun clientList(): List<ClientListDto> =
            repository.findActives()
                    .map { ClientListDto(it.id, it.name) }
}

