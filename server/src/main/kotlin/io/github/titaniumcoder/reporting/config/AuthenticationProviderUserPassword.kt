package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.Routines
import io.github.titaniumcoder.reporting.Tables.REPORTING_USER
import io.github.titaniumcoder.reporting.tables.records.ReportingUserRecord
import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.reactivestreams.Publisher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(private val create: DSLContext) : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null) {
            val identity = authenticationRequest.identity
            val secret = authenticationRequest.secret

            if (identity != null && secret != null && identity is String && secret is String) {
                return validatePassword(identity, secret)
            }
        }
        return Flowable.just(AuthenticationFailed())
    }

    private fun validatePassword(identity: String, secret: String): Flowable<AuthenticationResponse> {
        val user: ReportingUserRecord? = create.fetchOne(REPORTING_USER, REPORTING_USER.USERNAME.eq(identity).and(REPORTING_USER.ACTIVATED.isTrue))
        if (user != null && user.password != null) {
            val passwordCheck = user.password?.let { pw ->
                comparePassword(secret, pw)
            } ?: false
            if (passwordCheck) {
                val roles = mutableListOf<String>()
                if (user.admin) {
                    roles.add("ROLE_ADMIN")
                }
                if (user.canBook) {
                    roles.add("ROLE_BOOK")
                }
                if (user.canViewMoney) {
                    roles.add("ROLE_MONEY")
                }
                return Flowable.just(UserDetails(user.username, roles))
            }
        }

        return Flowable.just(AuthenticationFailed())
    }

    private fun comparePassword(password: String, storedPassword: String?): Boolean =
        create.select(Routines.crypt(password, storedPassword))
                .fetchOne().component1() == storedPassword
}
