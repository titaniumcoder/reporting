package io.github.titaniumcoder.toggl.reporting.config

//@Configuration
//@EnableWebFlux
//class WebConfig : WebFluxConfigurer {
//
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("/public", "classpath:/static/")
//                .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
//    }
//}
//
//@Component
//class CustomWebFilter : WebFilter {
//    override fun filter(exch: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
//            if (exch.request.uri.path == "/") {
//                chain.filter(
//                        exch
//                                .mutate()
//                                .request(
//                                        exch
//                                                .request
//                                                .mutate()
//                                                .path("/index.html")
//                                                .build()
//                                )
//                                .build()
//                )
//            } else chain.filter(exch)
//}
