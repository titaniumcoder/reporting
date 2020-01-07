package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.github.titaniumcoder.reporting.config.Roles.Booking
import io.github.titaniumcoder.reporting.user.UserDto
import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Transactional
class ProjectService(val repository: ProjectRepository, val clientRepository: ClientRepository, val userService: UserService) {
    @Secured(Admin)
    fun projects(): Flux<ProjectAdminDto> =
            repository.findAllSortedByName()
                    .flatMap { toAdminDto(it) }

    @Secured("isAuthenticated()")
    fun projectList(): Flux<ProjectList> {
        val currentUser = userService.reactiveCurrentUserDto()

        return repository
                .findAllSortedByName()
                .flatMap { p ->
                    currentUser
                            .map { u ->
                                u.admin || p.clientId in u.clients.map { c -> c.clientId }
                            }.flatMap {
                                if (it) {
                                    Mono.just(p)
                                } else {
                                    Mono.empty()
                                }
                            }
                }
                .flatMap {
                    clientRepository.findById(it.clientId)
                            .map { client ->
                                ProjectList(it.projectId, client.name, it.name)
                            }
                }
    }

    @Secured(Admin)
    fun saveProject(dto: ProjectAdminDto): Mono<ProjectAdminDto> {
        val project = Project(
                dto.id,
                dto.clientId,
                dto.active,
                dto.name,
                dto.maxMinutes,
                dto.rateInCentsPerHour,
                dto.billable
        )

        return repository.save(project)
                .flatMap { toAdminDto(it) }
    }

    @Secured(Admin, Booking)
    fun findProject(id: Long, user: UserDto): Mono<Project> {
        val project = repository.findById(id)

        return project
                .filter {
                    user.admin || it.clientId in user.clients.map { c -> c.clientId }
                }
                .map { it }
    }

    @Secured(Admin, Booking)
    fun findProjectAdminDto(id: Long, user: UserDto): Mono<ProjectAdminDto> =
            findProject(id, user)
                    .flatMap { toAdminDto(it) }

    @Secured(Admin, Booking)
    fun findClientForProject(id: Long, user: UserDto): Mono<Client> =
            findProject(id, user)
                    .flatMap {
                        clientRepository.findById(it.clientId)
                    }

    @Secured(Admin)
    fun deleteProject(id: Long): Mono<Void> {
        return repository.deleteById(id)
    }

    private fun toAdminDto(it: Project): Mono<ProjectAdminDto> =
            clientRepository.findById(it.clientId)
                    .map { client ->
                        ProjectAdminDto(
                                it.projectId,
                                client.clientId,
                                client.name,
                                it.active,
                                it.name,
                                it.maxMinutes,
                                it.rateInCentsPerHour,
                                it.billable
                        )
                    }

}

