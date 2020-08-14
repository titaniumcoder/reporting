package io.github.titaniumcoder.reporting.config

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class GoogleAuthenticationProvider : AuthenticationProvider{
    override fun authenticate(httpRequest: HttpRequest<*>?, ar: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        println("Received an authentication token with $ar")

        return flow<AuthenticationResponse> {
            throw AuthenticationException(AuthenticationFailed())
        }.asPublisher()
    }
}