package io.github.titaniumcoder.reporting.project

import io.github.titaniumcoder.reporting.config.Roles.Admin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ProjectController(val service: ProjectService) {
    @Secured(Admin)
    @GetMapping("/projects")
    fun projects() = service.projects()

    @Secured("isAuthenticated()")
    @GetMapping("/project-list")
    fun projectList() = service.projectList()

    @Secured(Admin)
    @PostMapping("/projects")
    fun save(@RequestBody @Validated project: ProjectAdminDto) = service.saveProject(project)

    @Secured(Admin)
    @DeleteMapping("/projects/{id}")
    fun delete(@PathVariable("id") id: Long) =
            service.deleteProject(id)
                    .map {
                        ResponseEntity.status(HttpStatus.NO_CONTENT).build<Void>()
                    }
}
