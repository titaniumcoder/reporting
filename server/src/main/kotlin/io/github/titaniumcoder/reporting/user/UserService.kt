package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.Client
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.providers.PasswordEncoder
import javax.inject.Singleton

@Singleton
class UserService(val repository: UserRepository, val encoder: PasswordEncoder) {
    fun listUsers(): List<UserDto> =
            repository.findAll()
                    .map { toDto(it) }

    fun saveUser(user: UserUpdateDto): UserDto {
        val newUser = User(
                user.username,
                user.password!! ,
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin,
                user.clients.map { Client(it, true, it) }
        )
        return toDto(repository.save(newUser))
    }

    fun validateUsernamePassword(username: String, password: String): User? =
            repository.findById(username)
                    .orElse(null)?.let { user ->
                        if (encoder.matches(password, user.password))
                            user
                        else
                            null
                    }

    fun updateUser(username: String, user: UserUpdateDto, auth: Authentication): UserDto? {
        println("Calling it with auth $auth")
        val roles = auth.attributes["roles"]
        val isAdmin =
                tryCheck<List<String>>(roles) {
                    this.contains("ROLE_ADMIN")
                }

        val allowed = auth.name == username || isAdmin

        if (!allowed) {
            // TODO throw unauthorized
            throw RuntimeException("Not allowed")
        }

        val currentUser = repository.findById(username).orElse(null)

        return currentUser?.let { u ->
            val fu = u.copy(
                    username = user.username,
                    email = user.email,
                    password = user.password ?: u.password,
                    admin = if (isAdmin) user.admin else u.admin,
                    canBook = if (isAdmin) user.canBook else u.canBook,
                    canViewMoney = if (isAdmin) user.canViewMoney else u.canViewMoney
            )
            toDto(repository.update(fu))
        }
    }

    private fun toDto(user: User): UserDto {
        return UserDto(
                user.username,
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin
        )
    }

    private inline fun <reified T> tryCheck(obj: Any?, block: T.() -> Boolean): Boolean =
        if (obj != null && obj is T) {
            block(obj)
        } else false
}
