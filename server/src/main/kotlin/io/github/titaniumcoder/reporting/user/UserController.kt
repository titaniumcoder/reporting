package io.github.titaniumcoder.reporting.user

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured

@Controller("/api")
class UserController(val service: UserService) {

    // @Secured("hasRole('ADMIN')")
    @Get("/users")
    fun list(): List<UserDto> {
        return service.listUsers()
    }

    // @Secured("hasRole('ADMIN')")
    @Post("/users")
    fun save(user: User): UserDto {
        return service.saveUser(user)
    }
}
