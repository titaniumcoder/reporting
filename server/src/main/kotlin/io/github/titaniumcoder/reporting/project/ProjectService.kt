package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.client.ClientRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProjectService(val repository: ProjectRepository, val clientRepository: ClientRepository) {
    fun projects(): List<ProjectAdminDto> =
            repository.findAll(Sort.by("name"))
                    .map { toAdminDto(it) }


    fun saveProject(dto: ProjectAdminDto): ProjectAdminDto {
        val client = clientRepository.findByIdOrNull(dto.clientId)
                ?: throw IllegalArgumentException("Unknown client ${dto.clientId} with name ${dto.clientName}")
        val project = Project(
                dto.id,
                client,
                dto.active,
                dto.name,
                dto.maxMinutes,
                dto.rateInCentsPerHour,
                dto.billable
        )
        return toAdminDto(repository.save(project))
    }

    fun deleteProject(id: Long) {
        repository.deleteById(id);
    }

    private fun toAdminDto(it: Project): ProjectAdminDto {
        return ProjectAdminDto(
                it.id,
                it.client.id,
                it.client.name,
                it.active,
                it.name,
                it.maxMinutes,
                it.rateInCentsPerHour,
                it.billable
        )
    }

}

