package io.github.titaniumcoder.toggl.reporting

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest

@MicronautTest
object InfoTest : WordSpec() {
    init {
        "info endpoint".should {
            val embeddedServer: EmbeddedServer = autoClose(ApplicationContext.run(EmbeddedServer::class.java))
            val client: HttpClient = autoClose(HttpClient.create(embeddedServer.url))

            "make git commit info appear in json" {

                val request: HttpRequest<Any> = HttpRequest.GET("/info")
                val rsp = client.toBlocking().exchange(request, Map::class.java)

                rsp.status shouldBe HttpStatus.OK

                val json: Map<String, Any> = rsp.body().castToMap()

                json["git"] shouldNotBe null

                val mapCommit = json["git"].castToMap()["commit"].castToMap()
                mapCommit shouldNotBe  null
                mapCommit["message"] shouldNotBe null
                mapCommit["time"] shouldNotBe null
                mapCommit["id"] shouldNotBe null
                mapCommit["user"] shouldNotBe null
                json["git"].castToMap()["branch"] shouldNotBe null
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Any?.castToMap(): Map<String, Any> =
            this as Map<String, Any>
}
