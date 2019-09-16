package io.github.titaniumcoder.toggl.reporting.config

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(private val config: TogglConfiguration) : AuthenticationProvider {

    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> =
            if (config.security.username == authenticationRequest.identity &&
                    config.security.password == authenticationRequest.secret) {
                Flowable.just(UserDetails(authenticationRequest.identity as String, listOf("ROLE_USER")))
            } else {
                Flowable.just(AuthenticationFailed())
            }
}
