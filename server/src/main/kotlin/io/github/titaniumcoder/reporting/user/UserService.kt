package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.project.Project
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Transactional
class UserService(val repository: UserRepository, val clientRepository: ClientRepository, val databaseClient: DatabaseClient) {
    fun usersExists(): Mono<Boolean> =
            repository.count().map { it > 0 }

    fun listUsers(): Flux<UserDto> =
            repository.findAll()
                    .flatMap { toDto(it).flux() }

    fun findByEmail(email: String): Mono<User> =
            repository.findById(email)

    fun saveUser(user: UserUpdateDto): Mono<UserDto> {
        return repository.existsById(user.email)
                .flatMap { exists ->
                    val newUser = User(
                            email = user.email,
                            canBook = user.canBook,
                            canViewMoney = user.canViewMoney,
                            admin = user.admin,
                            newUser = !exists
                    )

                    repository.save(newUser)
                            .flatMap { u ->
                                saveClients(u, user.clients)
                            }
                            .flatMap { toDto(it) }
                }
    }

    private fun saveClients(user: User, clients: List<String>): Mono<User> {
        val table = "Client_User"
        val clientIdField = "client_id"
        val emailField = "email"

        val currentIds = databaseClient.select()
                .from(table)
                .project(clientIdField)
                .matching(where(emailField).`is`(user.email))
                .map { t -> t.get(clientIdField, String::class.java)!! }
                .all()
                .collectList()

        return currentIds.flatMap { currentList ->
            val toDelete = currentList - clients
            val toInsert = clients - currentList

            val deleted =
                    if (toDelete.isNotEmpty()) {
                        databaseClient.delete()
                                .from(table)
                                .matching(where(emailField).`in`(*toDelete.toTypedArray()))
                                .fetch()
                                .rowsUpdated()
                    } else Mono.just(0)

            val inserted = Flux.fromIterable(toInsert)
                    .flatMap { c ->
                        databaseClient.insert()
                                .into(table)
                                .value(emailField, user.email)
                                .value(clientIdField, c)
                                .fetch()
                                .rowsUpdated()
                    }
                    .collectList()
                    .map { it.sum() }

            deleted.zipWith(inserted)
                    .map { it.t1 + it.t2 }
                    .log()
        }
                .map { user }
    }

    @Transactional
    fun reactiveCurrentUser(): Mono<User> {
        return ReactiveSecurityContextHolder
                .getContext()
                .map { it.authentication }
                .flatMap { findByEmail(it.principal as String) }
    }

    @Transactional
    fun reactiveCurrentUserDto(): Mono<UserDto> =
            reactiveCurrentUser()
                    .flatMap { toDto(it) }

    fun deleteUser(email: String): Mono<Void> {
        return repository.deleteById(email)
    }

    private fun toDto(user: User): Mono<UserDto> {
        val clients =
                clientRepository
                        .findAllForUser(user.email)
                        .map { it -> UserClientDto(it.clientId, it.name) }
                        .collectList()

        return clients.map {
            UserDto(
                    user.email,
                    user.canBook,
                    user.canViewMoney,
                    user.admin,
                    it
            )
        }
    }

    fun userHasAccessToClient(user: User, clientId: String): Mono<Boolean> =
            toDto(user).map { it.clients.map { c -> c.clientId }.contains(clientId) }

    fun userHasAccessToProject(user: User, project: Project): Mono<Boolean> =
            userHasAccessToClient(user, project.clientId)
}
