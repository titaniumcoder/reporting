package io.github.titaniumcoder.reporting.user

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/api/whoami")
    fun testLogin(@AuthenticationPrincipal(expression="name") name: String): String {
        return name
    }
}
