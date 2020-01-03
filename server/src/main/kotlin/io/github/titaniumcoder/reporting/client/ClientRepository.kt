package io.github.titaniumcoder.reporting.client

import org.springframework.data.jpa.repository.JpaRepository

interface ClientRepository : JpaRepository<Client, String>
