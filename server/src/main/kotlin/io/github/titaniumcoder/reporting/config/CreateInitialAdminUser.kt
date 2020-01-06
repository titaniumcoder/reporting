package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserService
import io.github.titaniumcoder.reporting.user.UserUpdateDto
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class CreateInitialAdminUser(val service: UserService, val config: ReportingConfiguration) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        service.usersExists()
                .subscribe { e ->
                    if (!e) {
                        val u = UserUpdateDto(config.adminEmail, true, true, true, listOf())
                        service.saveUser(u)
                        log.info("Created inital admin user with email \"${config.adminEmail}\"")
                    }
                }
    }
}
