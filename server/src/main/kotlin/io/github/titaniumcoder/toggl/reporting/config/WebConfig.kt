package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.util.concurrent.TimeUnit
import org.springframework.web.server.WebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.web.server.WebFilter


@Configuration
@EnableWebFlux
class WebConfig : WebFluxConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/public", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
    }
}

@Component
class CustomWebFilter : WebFilter {
    override fun filter(exch: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
            if (exch.request.uri.path == "/") {
                chain.filter(
                        exch
                                .mutate()
                                .request(
                                        exch
                                                .request
                                                .mutate()
                                                .path("/index.html")
                                                .build()
                                )
                                .build()
                )
            } else chain.filter(exch)
}
