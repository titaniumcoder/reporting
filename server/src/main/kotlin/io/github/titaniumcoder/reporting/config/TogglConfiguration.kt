package io.github.titaniumcoder.reporting.config

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("toggl")
class TogglConfiguration {
    @NotBlank
    lateinit var apiToken: String

    @NotBlank
    var workspaceId: Long = -1L

    @NotBlank
    lateinit var username: String

    @NotBlank
    lateinit var password: String
}
