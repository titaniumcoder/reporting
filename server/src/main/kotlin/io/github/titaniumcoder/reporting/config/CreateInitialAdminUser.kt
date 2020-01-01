package io.github.titaniumcoder.reporting.config

// TODO needs to be replaced completely
/*
@Singleton
class CreateInitialAdminUser(val service: UserService) {
    private val log = LoggerFactory.getLogger(CreateInitialAdminUser::class.java)

    @Async
    @EventListener
    fun onStartup(evt: StartupEvent) {
        if (service.listUsers().isEmpty()) {
            val u = UserUpdateDto("admin", "admin", "admin@test.org", true, true, true, listOf())
            service.saveUser(u)
            log.warn("Created Sample user with username \"admin\" and password \"password\", please change!!")
        }
    }
}
 */
