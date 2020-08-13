package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.validation.Validated

@Controller("/api")
class ProjectController(val service: ProjectService) {
    @Secured(Admin)
    @Get("/projects")
    fun projects() = service.projects()

    @Secured("isAuthenticated()")
    @Get("/project-list")
    fun projectList() = service.projectList()

    @Secured(Admin)
    @Post("/projects")
    @Validated
    fun save(@Body project: ProjectAdminDto) = service.saveProject(project)

    @Secured(Admin)
    @Delete("/projects/{id}")
    fun delete(@PathVariable("id") id: Long) {
            service.deleteProject(id)
    }
}
