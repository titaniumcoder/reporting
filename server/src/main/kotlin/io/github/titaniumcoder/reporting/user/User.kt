package io.github.titaniumcoder.reporting.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class User(
        @NotBlank
        @Email
        val email: String,

        val canBook: Boolean = true,

        val canViewMoney: Boolean = true,

        val admin: Boolean = true,

        @Transient val newUser: Boolean = true
)

data class UserDto(
        val email: String,
        val canBook: Boolean,
        val canViewMoney: Boolean,
        val admin: Boolean,
        val clients: List<UserClientDto> = listOf()
)

data class UserClientDto(
        val clientId: String,
        val name: String
)

data class UserUpdateDto(
        @NotBlank
        @Email
        val email: String,

        val admin: Boolean,
        val canBook: Boolean,
        val canViewMoney: Boolean,

        val clients: List<String>
)
