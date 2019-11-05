package io.github.titaniumcoder.toggl.reporting

import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer

object InfoTest : WordSpec() {
    init {
        "info endpoint" should {
            "make git commit info appear in json" {
                val embeddedServer: EmbeddedServer = autoClose(ApplicationContext.run(EmbeddedServer::class.java))
                val client: HttpClient = autoClose(HttpClient.create(embeddedServer.url))

                val request: HttpRequest<Any> = HttpRequest.GET("/info")
                val rsp = client.toBlocking().exchange(request, Map::class.java)

                rsp.status().shouldBe(HttpStatus.OK)

                /*

                val json: Map<String, Any> = rsp.body() as Map<String, Any>

                json["git"].shouldNotBeNull()

                val mapCommit = (json["git"] as Map<String, Any>)["commit"] as Map<String, Any>
                mapCommit.shouldNotBeNull()
                mapCommit["message"].shouldNotBeNull()
                mapCommit["time"].shouldNotBeNull()
                mapCommit["id"].shouldNotBeNull()
                mapCommit["user"].shouldNotBeNull()
                (json["git"] as Map<String, Any>)["branch"].shouldNotBeNull()

                 */
            }
        }
    }
}
