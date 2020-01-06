package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.config.Roles.Admin
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class UserController(val service: UserService) {

    @Secured("isAuthenticated()")
    @GetMapping("/current-user")
    fun me(auth: Authentication) = service.reactiveCurrentUserDto()

    @Secured(Admin)
    @GetMapping("/users")
    fun list() = service.listUsers()

    @Secured(Admin)
    @PostMapping("/users")
    fun save(@RequestBody @Validated user: UserUpdateDto) = service.saveUser(user)

    @Secured(Admin)
    @DeleteMapping("/users/{email}")
    fun delete(@PathVariable("email") email: String) =
            service.reactiveCurrentUser()
                    .flatMap { u ->
                        if (u.email == email) {
                            Mono.just(ResponseEntity.status(CONFLICT).build<Void>())
                        } else {
                            Mono.empty()
                        }
                    }.switchIfEmpty(
                            service.deleteUser(email)
                                    .map { ResponseEntity.status(NO_CONTENT).build<Void>() }
                    )
}
