package io.github.titaniumcoder.reporting.config

import io.micronaut.configuration.dbmigration.flyway.event.MigrationFinishedEvent
import io.micronaut.context.event.ApplicationEventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class InformAfterFlyway : ApplicationEventListener<MigrationFinishedEvent> {
    private val log: Logger = LoggerFactory.getLogger(InformAfterFlyway::class.java)

    override fun onApplicationEvent(event: MigrationFinishedEvent?) {
        log.info("Migration applied: " + event?.toString())
    }

}
