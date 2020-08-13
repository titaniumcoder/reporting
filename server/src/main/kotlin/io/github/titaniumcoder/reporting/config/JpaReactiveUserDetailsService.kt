package io.github.titaniumcoder.reporting.config

import io.github.titaniumcoder.reporting.user.UserRepository

//@Singleton
class JpaReactiveUserDetailsService(
        private val repository: UserRepository
) /* : ReactiveUserDetailsService  */ {
//    override fun findByUsername(username: String?): Mono<UserDetails> =
//            Mono.justOrEmpty(username)
//                    .flatMap { repository.findById(it) }
//                    .map {
//                        User.builder()
//                                .authorities(*roles(it))
//                                .username(it.email)
//                                .password("N/A")
//                                .build()
//                    }
//
//    private fun roles(user: io.github.titaniumcoder.reporting.user.User): Array<String> {
//        val l = mutableListOf(Roles.User)
//        if (user.admin)
//            l.add(Roles.Admin)
//        if (user.canBook)
//            l.add(Roles.Booking)
//        if (user.canViewMoney)
//            l.add(Roles.Money)
//
//        return l.toTypedArray()
//    }
}
