package io.github.titaniumcoder.toggl.reporting

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken

interface TestSupport {

    fun <B> MutableHttpRequest<B>.withToken(accessToken: String): MutableHttpRequest<B> {
        return this.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
    }

    fun loginToken(client: HttpClient, username: String, password: String): String {
        val creds = UsernamePasswordCredentials(username, password)
        val request = HttpRequest.POST("/api/login", creds)

        val rsp: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                BearerAccessRefreshToken::class.java)

        return rsp.body()!!.accessToken
    }
}
