package io.github.titaniumcoder.reporting.config

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

class WithJdbcSystemProperty : Condition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        return System.getenv().containsKey(JDBC_DATABASE_URL_ENV)
    }

    companion object {
        val JDBC_DATABASE_URL_ENV = "JDBC_DATABASE_URL"
    }
}
