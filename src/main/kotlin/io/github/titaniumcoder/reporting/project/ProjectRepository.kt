package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.model.Project
import javax.inject.Singleton

@Singleton
class ProjectRepository {
    fun findAllSortedByName(): List<Project> = TODO()

    // TODO implement this
    fun save(project: Project): Project = TODO()

    // TODO implement this
    fun findById(id: String): Project? = TODO()

    fun deleteById(id: String): Unit = TODO()
}
