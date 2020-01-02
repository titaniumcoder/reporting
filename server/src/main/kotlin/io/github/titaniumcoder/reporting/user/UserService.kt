package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.config.SecurityConfiguration
import org.apache.catalina.security.SecurityConfig
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(val repository: UserRepository) {
    fun usersExists(): Boolean =
            repository.count() > 0

    fun listUsers(): List<UserDto> =
            repository.findAll()
                    .map { toDto(it) }

    fun findByEmail(email: String): User? =
            repository.findByIdOrNull(email)

    fun saveUser(user: UserUpdateDto): UserDto {
        val newUser = User(
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin,
                user.clients.map { Client(it, true, it) }
        )
        return toDto(repository.save(newUser))
    }

    fun updateUser(username: String, user: UserUpdateDto, auth: Authentication): UserDto? {
        println("Calling it with auth $auth")
        val isAdmin = auth.authorities.map { it.authority }.contains("ROLE_ADMIN")

        val allowed = auth.name == username || isAdmin

        if (!allowed) {
            // TODO throw unauthorized
            throw RuntimeException("Not allowed")
        }

        val currentUser = repository.findById(username).orElse(null)

        return currentUser?.let { u ->
            val fu = u.copy(
                    email = user.email,
                    admin = if (isAdmin) user.admin else u.admin,
                    canBook = if (isAdmin) user.canBook else u.canBook,
                    canViewMoney = if (isAdmin) user.canViewMoney else u.canViewMoney
            )
            toDto(repository.save(fu))
        }
    }

    private fun toDto(user: User): UserDto {
        return UserDto(
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin
        )
    }

    fun currentUser(): UserDto? {
        val ctx = SecurityContextHolder.getContext()
        return ctx?.let { findByEmail(ctx.authentication.principal as String) }?.let { toDto(it) }
    }

    fun deleteUser(email: String): Unit {
        repository.deleteById(email);
    }

}
