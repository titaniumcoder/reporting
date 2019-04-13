package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@WebFluxTest
class ReportingControllerTest {
    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var service: ReportingService

    private val start: LocalDate = LocalDate.of(2019, 1, 1)
    private val end: LocalDate = LocalDate.of(2019, 2, 3)

    private val startTime: LocalDateTime = start.plusDays(2).atTime(13, 30)
    private val endTime: LocalDateTime = start.plusDays(2).atTime(13, 35)
    private val date: LocalDate = startTime.toLocalDate()

    private val rm = ViewModel.ReportingModel(
            "a", 1L, start, end, listOf(ViewModel.Project("a", 1)), listOf(
            listOf(
                    ViewModel.TimeEntry(1, date, "a", startTime, endTime, 1, "d", listOf("a", "b"))
            )
    ))

    @Test
    fun testEntries() {
        runBlocking {
            `when`(service.entries(1, null, null, false))
                    .thenReturn(rm)

            client.get()
                    .uri("/api/client/1")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test:test".toByteArray()))
                    .exchange()
                    .expectStatus().isOk
                    .expectBody().json("""{"client":"a","clientId":1,"from":"2019-01-01","to":"2019-02-03","projects":[{"name":"a","minutes":1}],"timeEntries":[[{"id":1,"day":"2019-01-03","project":"a","startdate":"2019-01-03T13:30:00","enddate":"2019-01-03T13:35:00","minutes":1,"description":"d","tags":["a","b"]}]]}""")
        }
    }

    @Test
    fun testExcel() {
        runBlocking {
            `when`(service.timesheet(1L, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 1, 3)))
                    .thenReturn(ExcelSheet("test", LocalDate.of(2019, 1, 1), "abc".toByteArray()))

            client.get()
                    .uri("/api/timesheet/1?from=2019-01-01&to=2019-01-03")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("test:test".toByteArray()))
                    .exchange()
                    .expectStatus().isOk
                    .expectHeader().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .expectBody().consumeWith { it.responseBody?.size ?: -1 > 0 }
        }
    }
}
