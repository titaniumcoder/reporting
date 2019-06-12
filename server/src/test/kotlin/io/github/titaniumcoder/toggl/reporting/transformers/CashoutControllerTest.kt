package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.util.*

@WebFluxTest
@Disabled("fix this tests authorization logic!")
class CashoutControllerTest {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var transformer: TransformerService

    @MockBean
    private lateinit var service: TogglService

    @Test
    fun testCash() {
        val summary = TogglModel.TogglSummary(100L, 100L, listOf(), listOf(TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("t"), 1, listOf(TogglModel.TogglCurrency("CHF", 100.0)))))

        runBlocking {
            `when`(service.summary(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 12, 31)))
                    .thenReturn(summary)

            `when`(transformer.cash(summary))
                    .thenReturn(listOf(ViewModel.Cashout("t", 100.0)))

            client
                    .get()
                    .uri("/api/cash?from=2018-01-01&to=2018-12-31")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test:test".toByteArray()))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody().json("""[{"client":"t", "amount": 100.0}]""")
        }
    }
}
