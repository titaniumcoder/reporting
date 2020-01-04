package io.github.titaniumcoder.reporting.client

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ClientService(val repository: ClientRepository) {
    fun clients(): List<Client> =
            repository.findAll(Sort.by("name"))

    fun client(id: Int): ClientDto = TODO()
    fun flagBilling(clientId: Int, from: LocalDate, to: LocalDate, billed: Boolean): Int = TODO()

    private fun calculateClientDto(client: Client): ClientDto = TODO()
}

