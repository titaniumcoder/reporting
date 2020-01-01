package io.github.titaniumcoder.reporting

import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.PlainJWT
import io.kotlintest.assertions.json.shouldContainJsonKey
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.annotation.MicronautTest

@MicronautTest
class BasicAuthSpec : WordSpec() {
    init {
        "Verify JWT authentication works" should {
            val embeddedServer: EmbeddedServer = autoClose(ApplicationContext.run(EmbeddedServer::class.java))
            val client: RxHttpClient = autoClose(RxHttpClient.create(embeddedServer.url))

            "Accessing a secured URL without authenticating" {
                shouldThrow<HttpClientResponseException> {
                    val request = HttpRequest.GET<Any>("/")
                    client.toBlocking().exchange(request, String::class.java)
                }
            }

            "the endpoint can be access with JWT obtained when Login endpoint is called with valid credentials" {
                val creds = UsernamePasswordCredentials("admin", "admin")
                val request = HttpRequest.POST("/api/login", creds)

                val rsp: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request,
                        BearerAccessRefreshToken::class.java)

                rsp.status() shouldBe HttpStatus.OK
                rsp.body()?.accessToken shouldNotBe null
                (JWTParser.parse(rsp.body()?.accessToken!!)!!).shouldBeInstanceOf<PlainJWT>()
                rsp.body()?.refreshToken shouldNotBe null
                (JWTParser.parse(rsp.body()?.refreshToken!!)!!).shouldBeInstanceOf<PlainJWT>()

                val accessToken: String = rsp.body()!!.accessToken
                val requestWithAuthorization = HttpRequest.GET<Any>("/health").header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                val response: HttpResponse<String> = client.toBlocking().exchange(requestWithAuthorization, String::class.java)

                response.status() shouldBe HttpStatus.OK

                (response.body()!!).shouldContainJsonKey("$.name")
            }
        }
    }
}
