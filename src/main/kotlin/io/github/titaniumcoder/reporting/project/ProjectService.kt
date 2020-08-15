package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.client.ClientRepository
import io.github.titaniumcoder.reporting.model.Client
import io.github.titaniumcoder.reporting.model.Project
import io.micronaut.security.annotation.Secured
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Singleton

@Singleton
// FIXME @Transactional
class ProjectService(
        private val repository: ProjectRepository,
        private val clientRepository: ClientRepository
) {
    @Secured("isAuthenticated()")
    fun findProject(id: String): Project? = repository.findById(id)

    @Secured("isAuthenticated()")
    suspend fun findClientForProject(id: String): Client? =
            clientRepository.findAllSortedById()
                    .filter { p -> p.projects.map { it.id }.find { it == id } != null }
                    .firstOrNull()
}
