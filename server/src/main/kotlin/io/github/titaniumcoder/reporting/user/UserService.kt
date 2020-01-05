package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.project.Project
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional
class UserService(val repository: UserRepository, val clientRepository: ClientRepository) {
    fun usersExists(): Boolean =
            repository.count() > 0

    fun listUsers(): List<UserDto> =
            repository.findAll()
                    .map { toDto(it) }

    fun findByEmail(email: String): User? =
            repository.findByIdOrNull(email)

    fun saveUser(user: UserUpdateDto): UserDto {
        val clients = user.clients.map { clientRepository.findByIdOrNull(it) }

        if (clients.any { it == null }) {
            throw java.lang.RuntimeException("Unknown client in $user")
        }

        val newUser = User(
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin,
                user.clients.mapNotNull { clientRepository.findByIdOrNull(it) }
        )
        return toDto(repository.save(newUser))
    }

    fun currentUser(): User? {
        val a1 = SecurityContextHolder.getContext().authentication

        return findByEmail(a1.principal as String)
    }

    fun currentUserDto(): UserDto? = currentUser()?.let { toDto(it) }

    fun deleteUser(email: String) {
        repository.deleteById(email)
    }

    private fun toDto(user: User): UserDto {
        return UserDto(
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin,
                user.clients.map { UserClient(it.id, it.name) }
        )
    }

    fun userHasAccessToClient(user: User, client: Client): Boolean =
            user.clients.contains(client)

    fun userHasAccessToProject(user: User, project: Project): Boolean =
            userHasAccessToClient(user, project.client)
}
