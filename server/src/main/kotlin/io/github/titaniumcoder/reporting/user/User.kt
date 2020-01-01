package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.Client
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class User(
        @NotBlank
        @Size(max = 100)
        val username: String,

        @Size(max = 100)
        val password: String,

        @NotBlank
        @Email
        val email: String,

        val canBook: Boolean = true,

        val canViewMoney: Boolean = true,

        val admin: Boolean = true,

        val clients: List<Client> = listOf()
)

data class UserDto(
        val username: String,
        val email: String,
        val canBook: Boolean,
        val canViewMoney: Boolean,
        val admin: Boolean,
        val clients: List<Client> = listOf()
)
