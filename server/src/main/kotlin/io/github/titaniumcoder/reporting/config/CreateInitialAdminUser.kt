package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserService
import io.github.titaniumcoder.reporting.user.UserUpdateDto
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class CreateInitialAdminUser(
        private val service: UserService,
        private val dataSource: DataSource,
        val config: ReportingConfiguration
) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    // @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        val flyway =
                Flyway
                        .configure()
                        .dataSource(dataSource)
                        .load()

        val version = flyway.migrate()

        log.info("Executed Flyway, version is now $version")

        val exists = service.usersExists()
        if (!exists) {
            val u = UserUpdateDto(config.adminEmail, true, true, true, listOf())
            service.saveUser(u)
            log.info("Created inital admin user with email \"${config.adminEmail}\"")
        }
    }
}
