package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserService
import io.github.titaniumcoder.reporting.user.UserUpdateDto
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CreateInitialAdminUser(val service: UserService) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        if (!service.usersExists()) {
            val u = UserUpdateDto("admin", "admin", "admin@test.org", true, true, true, listOf())
            service.saveUser(u)
            log.warn("Created Sample user with username \"admin\" and password \"password\", please change!!")
        }
    }
}
