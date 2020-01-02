package io.github.titaniumcoder.reporting.user

import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController(val service: UserService) {

    @Secured("isAuthenticated()")
    @GetMapping("/current-user")
    fun me(auth: Authentication): UserDto? {
        return service.currentUser()
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/users")
    fun list(): List<UserDto> {
        return service.listUsers()
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/users")
    fun save(@RequestBody user: UserUpdateDto): UserDto {
        return service.saveUser(user)
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/users/{email}")
    fun delete(@PathVariable("email") email: String): org.springframework.http.HttpStatus {
        service.deleteUser(email)
        return NO_CONTENT;
    }
}
