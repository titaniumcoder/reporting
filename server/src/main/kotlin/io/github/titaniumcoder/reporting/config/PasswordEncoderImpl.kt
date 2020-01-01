package io.github.titaniumcoder.reporting.config

import io.micronaut.security.authentication.providers.PasswordEncoder
import org.apache.commons.codec.digest.DigestUtils
import javax.inject.Singleton

@Singleton
class PasswordEncoderImpl : PasswordEncoder {
    override fun encode(rawPassword: String): String =
        DigestUtils.md5Hex(rawPassword)

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        DigestUtils.md5Hex(rawPassword) == encodedPassword
}
