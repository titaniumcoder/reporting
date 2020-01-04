package io.github.titaniumcoder.reporting.client

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ClientRepository : JpaRepository<Client, String> {
    @Query("select c from Client c where c.active = true order by c.name")
    fun findActives(): List<Client>
}
