package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserService
import io.github.titaniumcoder.reporting.user.UserUpdateDto
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CreateInitialAdminUser(val service: UserService) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    @Async
    @EventListener
    fun onStartup(evt: StartupEvent) {
        if (service.listUsers().isEmpty()) {
            val u = UserUpdateDto("admin", "admin", "admin@test.org", true, true, true, listOf())
            service.saveUser(u)
            log.warn("Created Sample user with username \"admin\" and password \"password\", please change!!")
        }
    }
}
