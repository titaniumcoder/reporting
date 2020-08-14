package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.user.UserDto
import io.github.titaniumcoder.reporting.user.UserService
import io.micronaut.security.annotation.Secured
import javax.inject.Singleton

@Singleton
// FIXME @Transactional
class ProjectService(
        private val repository: ProjectRepository,
        private val clientRepository: ClientRepository,
        private val userService: UserService
) {
    @Secured("isAuthenticated()")
    fun projects(): List<ProjectAdminDto> =
            repository.findAllSortedByName().mapNotNull { toAdminDto(it) }

    @Secured("isAuthenticated()")
    fun projectList(): List<ProjectList> {
        val currentUser = userService.reactiveCurrentUserDto()

        return repository
                .findAllSortedByName()
                .mapNotNull { p ->
                    currentUser
                            ?.let { u ->
                                u.admin || p.clientId in u.clients.map { c -> c.clientId }
                            }?.let {
                                if (it) {
                                    p
                                } else {
                                    null
                                }
                            }
                }
                .mapNotNull {
                    clientRepository.findById(it.clientId)
                            ?.let { client ->
                                ProjectList(it.projectId, client.name, it.name, it.billable)
                            }
                }
    }

    @Secured("isAuthenticated()")
    fun saveProject(dto: ProjectAdminDto): ProjectAdminDto? {
        val project = Project(
                dto.id,
                dto.clientId,
                dto.active,
                dto.name,
                dto.maxMinutes,
                dto.rateInCentsPerHour,
                dto.billable
        )

        return toAdminDto(repository.save(project))
    }

    @Secured("isAuthenticated()")
    fun findProject(id: Long, user: UserDto): Project? {
        val project = repository.findById(id)

        return project
                ?.let {
                    if (user.admin || it.clientId in user.clients.map { c -> c.clientId })
                        it
                    else null
                }
    }

    @Secured("isAuthenticated()")
    fun findProjectAdminDto(id: Long, user: UserDto): ProjectAdminDto? = findProject(id, user)?.let { toAdminDto(it) }

    @Secured("isAuthenticated()")
    fun findClientForProject(id: Long, user: UserDto): Client? =
            findProject(id, user)?.let { clientRepository.findById(it.clientId) }

    @Secured("isAuthenticated()")
    fun deleteProject(id: Long) {
        return repository.deleteById(id)
    }

    private fun toAdminDto(it: Project): ProjectAdminDto? =
            clientRepository.findById(it.clientId)
                    ?.let { client ->
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

