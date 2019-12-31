package io.github.titaniumcoder.reporting.client

import java.time.LocalDate
import javax.inject.Singleton

@Singleton
class ClientService {
    fun clients(): List<Client> = TODO()
    fun client(id: Int): ClientDto = TODO()
    fun flagBilling(clientId: Int, from: LocalDate, to: LocalDate, billed: Boolean): Int = TODO()

    private fun calculateClientDto(client: Client): ClientDto = TODO()
}

