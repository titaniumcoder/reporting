package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.config.WithJdbcSystemProperty.Companion.JDBC_DATABASE_URL_ENV
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.*
import org.springframework.core.type.AnnotatedTypeMetadata
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
class R2dbcConfiguration {
    // TODO FIX this
    @Bean
    @Conditional(WithJdbcSystemProperty::class)
    fun connectionFactory(properties: R2dbcProperties,
                          customizers: List<ConnectionFactoryOptionsBuilderCustomizer>): ConnectionFactory? {
        properties.url ="r2dbc:" + System.getenv(JDBC_DATABASE_URL_ENV)

        return ConnectionFactoryBuilder
                .create(properties)
                .customize(customizers)
                .build()
    }
}

class WithJdbcSystemProperty : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        return System.getenv().containsKey(JDBC_DATABASE_URL_ENV)
    }

    companion object {
        val JDBC_DATABASE_URL_ENV = "JDBC_DATABASE_URL"
    }
}
