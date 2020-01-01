package io.github.titaniumcoder.reporting.config

// TODO needs to be replaced completely
/*
@Singleton
class AuthenticationProviderUserPassword(private val service: UserService) : AuthenticationProvider {
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
        val user = service.validateUsernamePassword(identity, secret)
        if (user != null) {
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

        return Flowable.just(AuthenticationFailed())
    }
}
 */
