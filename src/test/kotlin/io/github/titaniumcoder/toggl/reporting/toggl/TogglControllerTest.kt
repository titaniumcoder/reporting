package io.github.titaniumcoder.toggl.reporting.toggl

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@WebFluxTest
class TogglControllerTest {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var service: TogglService

    @Test
    fun testClients() {
        runBlocking {
            `when`(service.clients())
                    .thenReturn(listOf(TogglModel.Client(1, 1, "fred", "feuerstein"), TogglModel.Client(2, 2, "wilma", "feuerstein"), TogglModel.Client(3, 3, "bart", null)))

            client
                    .get()
                    .uri("/api/clients")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test:test".toByteArray()))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody().json("""[{"id":1, "wid": 1, "name": "fred", "notes": "feuerstein"}, {"id":2, "wid": 2, "name": "wilma", "notes": "feuerstein"}, {"id":3, "wid": 3, "name": "bart", "notes": null}]""")
        }
    }

    @Test
    @Disabled
    fun testTagBilledEntry() {
        // @PutMapping("/api/tag/{entry}")
        TODO()
    }

    @Test
    @Disabled
    fun testTagUnbilledEntry() {
        // @DeleteMapping("/api/tag/{entry}")
        TODO()
    }

    @Test
    @Disabled
    fun testTagBilledClient() {
        // @PutMapping("/api/service/{clientId}/billed?from&to")
        TODO()
    }

    @Test
    @Disabled
    fun testTagUnbilledClient() {
        // @DeleteMapping("/api/service/{clientId}/billed?from&to")
        TODO()
    }
}
