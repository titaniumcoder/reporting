package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserService
import io.github.titaniumcoder.reporting.user.UserUpdateDto
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.net.URI

@Service
class CreateInitialAdminUser(val service: UserService, val config: ReportingConfiguration, val r2Config: R2dbcProperties) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        val flyway =
                if (System.getenv().containsKey("DATABASE_URL")) {
                    val url = URI.create(System.getenv("DATABASE_URL"))
                    val userinfo = url.rawUserInfo?.split(":") ?: listOf(r2Config.username, r2Config.password)

                    Flyway
                            .configure()
                            .dataSource("jdbc:postgresql://${url.host}:${url.port}${url.rawPath}", userinfo[0], userinfo[1])
                            .load()
                } else {
                    Flyway
                            .configure()
                            .dataSource(r2Config.url.replace("r2dbc:", "jdbc:"), r2Config.username, r2Config.password)
                            .load()
                }

        val version = flyway.migrate()

        log.info("Executed Flyway, version is now $version")

        service.usersExists()
                .subscribe { e ->
                    if (!e) {
                        val u = UserUpdateDto(config.adminEmail, true, true, true, listOf())
                        service.saveUser(u)
                                .subscribe()
                        log.info("Created inital admin user with email \"${config.adminEmail}\"")
                    }
                }
    }
}
