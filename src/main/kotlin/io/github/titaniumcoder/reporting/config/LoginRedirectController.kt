package io.github.titaniumcoder.reporting.config

import io.micronaut.core.io.ResourceResolver
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.temporaryRedirect
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import io.micronaut.security.utils.SecurityService
import java.net.URI

@Controller
class LoginRedirectController(
        private val securityService: SecurityService?
) {
    @Get
    fun index(): HttpResponse<StreamedFile> {
        return if (securityService == null || securityService.isAuthenticated) {
            val loader = ResourceResolver().getLoader(ClassPathResourceLoader::class.java).get()
            val resource = loader.getResource("classpath:static/index.html")
            return HttpResponse.ok(StreamedFile(resource.get()))
        } else {
            temporaryRedirect(URI.create("/oauth/login/google"))
        }
    }
}
