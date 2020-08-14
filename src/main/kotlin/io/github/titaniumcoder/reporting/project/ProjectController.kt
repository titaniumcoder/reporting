package io.github.titaniumcoder.reporting.project

import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.validation.Validated

@Controller("/api")
class ProjectController(
        private val service: ProjectService
) {
    @Secured("isAuthenticated()")
    @Get("/projects")
    fun projects() = service.projects()

    @Secured("isAuthenticated()")
    @Get("/project-list")
    fun projectList() = service.projectList()

    @Secured("isAuthenticated()")
    @Post("/projects")
    @Validated
    fun save(@Body project: ProjectAdminDto) = service.saveProject(project)

    @Secured("isAuthenticated()")
    @Delete("/projects/{id}")
    fun delete(@PathVariable("id") id: Long) {
            service.deleteProject(id)
    }
}
