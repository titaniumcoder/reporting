package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import java.net.URI

// FIXME this is a hack$
@Configuration
class StaticHomepage {
    @Bean
    fun indexRouter(): RouterFunction<ServerResponse> {
        val redirectToIndex =
                ServerResponse
                        .temporaryRedirect(URI("/index.html"))
                        .build()

        return router {
            GET("/") {
                redirectToIndex // also you can create request here
            }
        }
    }
}
