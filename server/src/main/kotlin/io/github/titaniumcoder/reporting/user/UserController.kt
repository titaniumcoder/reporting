package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.config.Roles.Admin
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController(val service: UserService) {

    @Secured("isAuthenticated()")
    @GetMapping("/current-user")
    fun me(auth: Authentication): UserDto? {
        return service.currentUserDto()
    }

    @Secured(Admin)
    @GetMapping("/users")
    fun list(): List<UserDto> {
        return service.listUsers()
    }

    @Secured(Admin)
    @PostMapping("/users")
    fun save(@RequestBody @Validated user: UserUpdateDto): UserDto {
        return service.saveUser(user)
    }

    @Secured(Admin)
    @DeleteMapping("/users/{email}")
    fun delete(@PathVariable("email") email: String): ResponseEntity<Unit> {
        if (SecurityContextHolder.getContext().authentication.principal == email) {
            return ResponseEntity.status(CONFLICT).build()
        }

        service.deleteUser(email)
        return ResponseEntity.status(NO_CONTENT).build()
    }
}
