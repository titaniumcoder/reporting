package io.github.titaniumcoder.reporting.config

// FIXME how do we do this?
//@Configuration
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
class SecurityConfiguration(
//        val securityContextRepository: ServerSecurityContextRepository,
//        val authenticationManager: ReactiveAuthenticationManager
) {

//    @Bean
//    fun securitygWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
//        return http
//                .cors().disable()
//                .csrf().disable()
//                .formLogin().disable()
//                .httpBasic().disable()
//                .exceptionHandling()
//                .authenticationEntryPoint { swe, _ ->
//                    Mono.fromRunnable {
//                        swe.response.statusCode = HttpStatus.UNAUTHORIZED
//                    }
//                }
//                .accessDeniedHandler { swe, _ ->
//                    Mono.fromRunnable {
//                        swe.response.statusCode = HttpStatus.FORBIDDEN
//                    }
//                }
//                .and()
//                .securityContextRepository(securityContextRepository)
//                .authenticationManager(authenticationManager)
//                .authorizeExchange()
//                .pathMatchers(HttpMethod.OPTIONS).permitAll()
//                .pathMatchers("/", "/index.html", "/*.json", "/favicon.ico", "/*.js", "/static/**", "/robots.txt", "/error", "/sse/**").permitAll()
//                .anyExchange().authenticated()
//                .and().build()
//    }
}

