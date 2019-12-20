package io.github.titaniumcoder.reporting.config

import io.micronaut.security.authentication.*
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class AuthenticationProviderUserPassword(private val configuration: TogglConfiguration) : AuthenticationProvider {
    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        if (authenticationRequest != null && authenticationRequest.identity != null && authenticationRequest.secret != null) {
            if (authenticationRequest.identity == configuration.username && authenticationRequest.secret == configuration.password) {
                return Flowable.just<AuthenticationResponse>(UserDetails(authenticationRequest.identity as String, ArrayList()))
            }
        }
        return Flowable.just(AuthenticationFailed())
    }
}
