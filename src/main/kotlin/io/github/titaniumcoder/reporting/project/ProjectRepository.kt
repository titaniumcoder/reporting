package io.github.titaniumcoder.reporting.project

import javax.inject.Singleton

@Singleton
class ProjectRepository {
    fun findAllSortedByName(): List<Project> = TODO()

    // TODO implement this
    fun save(project: Project): Project = TODO()

    // TODO implement this
    fun findById(id: Long): Project? = TODO()

    fun deleteById(id: Long): Unit = TODO()
}
