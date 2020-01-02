package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.Client
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "reporting_user")
data class User(
        @NotBlank
        @Email
        @Id
        val email: String,

        @Column(name = "can_book")
        val canBook: Boolean = true,

        @Column(name = "can_view_money")
        val canViewMoney: Boolean = true,

        val admin: Boolean = true,

        @ManyToMany
        @JoinTable(
                name = "client_user",
                joinColumns = [JoinColumn(name = "email")],
                inverseJoinColumns = [JoinColumn(name = "client_id")]
        )
        val clients: List<Client> = listOf()
)

data class UserDto(
        val email: String,
        val canBook: Boolean,
        val canViewMoney: Boolean,
        val admin: Boolean,
        val clients: List<Client> = listOf()
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
