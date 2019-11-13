package io.github.titaniumcoder.toggl.reporting

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.endpoints.TokenRefreshRequest
import io.micronaut.security.token.jwt.render.AccessRefreshToken
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.annotation.MicronautTest
import java.lang.Thread.sleep

@MicronautTest
class OauthAccessTokenSpec : WordSpec() {
    init {
        "Verify JWT access token refresh works" should {
            val embeddedServer: EmbeddedServer = autoClose(ApplicationContext.run(EmbeddedServer::class.java))
            val client: RxHttpClient = autoClose(RxHttpClient.create(embeddedServer.url))

            "you can refresh JWT access token with /oauth/access_token endpoint" {
                val creds = UsernamePasswordCredentials("sherlock", "password")
                val request = HttpRequest.POST("/login", creds)

                val rsp: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                        BearerAccessRefreshToken::class.java)

                rsp.status shouldBe HttpStatus.OK

                val refreshToken: String = rsp.body()!!.refreshToken
                val accessToken: String = rsp.body()!!.accessToken

                // TODO fix this
                sleep(1_000) // sleep for one second to give time for the issued at `iat` Claim to change

                val refreshTokenRequest: HttpRequest<TokenRefreshRequest> = HttpRequest.POST("/oauth/access_token",
                        TokenRefreshRequest("refresh_token", refreshToken))
                val response: HttpResponse<AccessRefreshToken> = client.toBlocking().exchange(refreshTokenRequest, AccessRefreshToken::class.java)

                response.status shouldBe HttpStatus.OK
                response.body()?.accessToken shouldNotBe null
                response.body()?.accessToken shouldNotBe accessToken
            }
        }
    }
}
