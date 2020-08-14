package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.config.Roles.Admin
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.validation.Validated
import java.security.Principal

@Controller("/api")
class UserController(
        private val service: UserService
) {

    @Secured("isAuthenticated()")
    @Get("/current-user")
    fun me() = service.reactiveCurrentUserDto()

    @Secured(Admin)
    @Get("/users")
    fun list() = service.listUsers()

    @Secured(Admin)
    @Post("/users")
    @Validated
    fun save(@Body user: UserUpdateDto) = service.saveUser(user)

    @Secured(Admin)
    @Delete("/users/{email}")
    fun delete(@PathVariable("email") email: String): HttpStatus {
        val user = service.reactiveCurrentUser()

        return when (user) {
            null -> {
                HttpStatus.UNAUTHORIZED
            }
            else -> {
                if (user.email == email) {
                    HttpStatus.CONFLICT
                } else {
                    service.deleteUser(email)
                    HttpStatus.NO_CONTENT
                }
            }
        }
    }
}
