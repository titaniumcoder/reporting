package io.github.titaniumcoder.toggl.reporting.config

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LoginController {

    @PostMapping("/login")
    fun login() {

    }
}
