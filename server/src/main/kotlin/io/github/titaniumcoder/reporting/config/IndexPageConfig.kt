package io.github.titaniumcoder.reporting.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class IndexPageConfig {
    @Value("classpath:/static/index.html")
    private lateinit var indexHtml: Resource

    @Bean
    fun indexRouter(): RouterFunction<ServerResponse> {
        return router {
            GET("/") {
                ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml)
            }
        }
    }
}
