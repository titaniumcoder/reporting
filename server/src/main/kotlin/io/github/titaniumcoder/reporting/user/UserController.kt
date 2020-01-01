package io.github.titaniumcoder.reporting.user

import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule

@Controller("/api")
class UserController(val service: UserService) {

    @Secured("ROLE_ADMIN")
    @Get("/users")
    fun list(): List<UserDto> {
        return service.listUsers()
    }

    @Secured("ROLE_ADMIN")
    @Post("/users")
    fun save(@Body user: UserUpdateDto): UserDto {
        return service.saveUser(user)
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put("/users/{username}")
    fun update(@PathVariable("username") username: String, @Body user: UserUpdateDto, auth: Authentication): UserDto? {
        return service.updateUser(username, user, auth)
    }
}
