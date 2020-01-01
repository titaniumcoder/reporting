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
        @Size(max = 100)
        @Id
        val username: String,

        @Size(max = 100)
        val password: String,

        @NotBlank
        @Email
        val email: String,

        @Column(name = "can_book")
        val canBook: Boolean = true,

        @Column(name = "can_view_money")
        val canViewMoney: Boolean = true,

        val admin: Boolean = true,

        @ManyToMany
        @JoinTable(
                name = "client_user",
                joinColumns = [JoinColumn(name = "username")],
                inverseJoinColumns = [JoinColumn(name = "client_id")]
        )
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

data class UserUpdateDto(
        @NotBlank
        @Size(min = 2, max = 100)
        val username: String,

        @NotBlank
        @Email
        val email: String,

        @Size(min = 8, max = 100)
        val password: String?,

        val admin: Boolean,
        val canBook: Boolean,
        val canViewMoney: Boolean,

        val clients: List<String>
)
