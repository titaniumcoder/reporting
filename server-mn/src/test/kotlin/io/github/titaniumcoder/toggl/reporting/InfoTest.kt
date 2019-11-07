package io.github.titaniumcoder.toggl.reporting

import io.kotlintest.specs.WordSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@MicronautTest
object InfoTest : WordSpec() {
    init {
        "info endpoint".should {
            val embeddedServer: EmbeddedServer = autoClose(ApplicationContext.run(EmbeddedServer::class.java))
            val client: HttpClient = autoClose(HttpClient.create(embeddedServer.url))

            "make git commit info appear in json" {

                val request: HttpRequest<Any> = HttpRequest.GET("/info")
                val rsp = client.toBlocking().exchange(request, Map::class.java)

                assertEquals(HttpStatus.OK, rsp.status)

                val json: Map<String, Any> = rsp.body().castToMap()

                assertNotNull(json["git"])

                val mapCommit = json["git"].castToMap()["commit"].castToMap()
                assertNotNull(mapCommit)
                assertNotNull(mapCommit["message"])
                assertNotNull(mapCommit["time"])
                assertNotNull(mapCommit["id"])
                assertNotNull(mapCommit["user"])
                assertNotNull(json["git"].castToMap()["branch"])
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Any?.castToMap(): Map<String, Any> =
            this as Map<String, Any>
}
