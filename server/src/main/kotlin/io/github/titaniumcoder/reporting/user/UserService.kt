package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.project.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
// FIXME @Transactional
class UserService(
        private val repository: UserRepository,
        private val clientRepository: ClientRepository
) {
    fun usersExists(): Boolean = repository.count() > 0

    fun listUsers() = repository.findAll().map { toDto(it) }

    fun findByEmail(email: String) = repository.findById(email)

    fun saveUser(user: UserUpdateDto): UserDto {
        val exists = repository.existsById(user.email)
        val newUser = User(
                email = user.email,
                canBook = user.canBook,
                canViewMoney = user.canViewMoney,
                admin = user.admin,
                newUser = !exists
        )

        val u = repository.save(newUser)

        saveClients(u, user.clients)
        return toDto(u)
    }

    private fun saveClients(user: User, clients: List<String>): User {
        val table = "Client_User"
        val clientIdField = "client_id"
        val emailField = "email"

        // FIXME implement this in the repository!!
        val currentIds: List<String> = clientRepository.currentIdsForUser(user.email)

        val toDelete = currentIds - clients
        val toInsert = clients - currentIds

        val deleted =
                if (toDelete.isNotEmpty()) {
                    // FIXME use JDBI
                    toDelete.size
                    /*
                    databaseClient.delete()
                            .from(table)
                            .matching(where(emailField).`in`(*toDelete.toTypedArray()))
                            .fetch()
                            .rowsUpdated()
                            */
                } else 0

        val inserted = toInsert
                .map { c ->
                    1

                    // FIXME use JDBI
                    /*
                    databaseClient.insert()
                            .into(table)
                            .value(emailField, user.email)
                            .value(clientIdField, c)
                            .fetch()
                            .rowsUpdated()

                     */
                }
                .sum()

        log.info("Inserted $inserted and delete $deleted records")

        return user
    }

    // FIXME @Transactional
    fun reactiveCurrentUser(): User? {
        TODO("not yet translated")
//        return ReactiveSecurityContextHolder
//                .getContext()
//                .map { it.authentication }
//                .flatMap { findByEmail(it.principal as String) }
    }

    // FIXME @Transactional
    fun reactiveCurrentUserDto(): UserDto? = reactiveCurrentUser()?.let { toDto(it) }

    fun deleteUser(email: String) {
        return repository.deleteById(email)
    }

    fun toDto(user: User): UserDto {
        val clients =
                clientRepository
                        .findAllForUser(user.email)
                        .map { UserClientDto(it.clientId, it.name) }

        return UserDto(
                user.email,
                user.canBook,
                user.canViewMoney,
                user.admin,
                clients
        )
    }

    fun userHasAccessToClient(user: User, clientId: String): Boolean =
            userHasAccessToClient(toDto(user), clientId)

    fun userHasAccessToClient(user: UserDto, clientId: String): Boolean =
            user.clients.map { c -> c.clientId }.contains(clientId)

    fun userHasAccessToProject(user: UserDto, project: Project): Boolean =
            userHasAccessToClient(user, project.clientId)

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
