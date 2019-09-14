package io.github.titaniumcoder.toggl.reporting.config;

import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Collections;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
    private final TogglConfiguration config;

    public AuthenticationProviderUserPassword(TogglConfiguration config) {
        this.config = config;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        if (config.getSecurity().getUsername().equals(authenticationRequest.getIdentity()) &&
                config.getSecurity().getPassword().equals(authenticationRequest.getSecret())) {
            return Flowable.just(new UserDetails((String) authenticationRequest.getIdentity(), Collections.singletonList("ROLE_USER")));
        } else {
            return Flowable.just(new AuthenticationFailed());
        }
    }
}
