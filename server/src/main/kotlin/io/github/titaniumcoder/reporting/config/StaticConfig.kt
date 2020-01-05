package io.github.titaniumcoder.reporting.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import java.net.URI

@Configuration
class StaticConfig {
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
