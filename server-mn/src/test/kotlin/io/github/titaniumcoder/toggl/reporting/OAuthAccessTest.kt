package io.github.titaniumcoder.toggl.reporting

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
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OauthAccessTokenSpec: Spek({
    describe("Verify JWT access token refresh works") {

        var embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        var client: RxHttpClient = RxHttpClient.create(embeddedServer.url)

        it("you can refresh JWT access token with /oauth/access_token endpoint") {
            val creds = UsernamePasswordCredentials("sherlock", "password")
            val request = HttpRequest.POST("/login", creds)

            val rsp: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                    BearerAccessRefreshToken::class.java)

            assertEquals(rsp.status()!!, HttpStatus.OK)

            val refreshToken: String = rsp.body()!!.refreshToken
            val accessToken: String = rsp.body()!!.accessToken
            sleep(1_000) // sleep for one second to give time for the issued at `iat` Claim to change
            val refreshTokenRequest: HttpRequest<TokenRefreshRequest> = HttpRequest.POST("/oauth/access_token",
                    TokenRefreshRequest("refresh_token", refreshToken))
            val response: HttpResponse<AccessRefreshToken> = client.toBlocking().exchange(refreshTokenRequest, AccessRefreshToken::class.java)

            assertEquals(response.status()!!, HttpStatus.OK)
            assertNotNull(response.body()!!.accessToken)
            assertTrue(response.body()!!.accessToken != accessToken)
        }

        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})
