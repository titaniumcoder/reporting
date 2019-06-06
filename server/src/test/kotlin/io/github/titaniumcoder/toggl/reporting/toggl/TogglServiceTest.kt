package io.github.titaniumcoder.toggl.reporting.toggl

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime


// TODO find a better way than just run it inside a runBlock for everything
@ExtendWith(SpringExtension::class)
class TogglServiceTest {
    @TestConfiguration
    class TogglClientTestConfiguration {
        @Bean
        fun togglClient(webClient: TogglWebClient): TogglService =
                TogglService(webClient)
    }

    @Autowired
    private lateinit var togglService: TogglService

    @MockBean
    private lateinit var webClient: TogglWebClient

    val zurichZone = ZoneId.of("Europe/Zurich")

    private val sampleStartDate: LocalDate = LocalDate.of(2019, 2, 10)
    private val sampleEndDate: LocalDate = LocalDate.of(2019, 10, 15)

    @Test
    fun testClients() {
        runBlocking {
            `when`(webClient.clients())
                    .thenReturn(
                            listOf(
                                    TogglModel.Client(1L, 1L, "First", "First Notes"),
                                    TogglModel.Client(2L, 2L, "Second, null", null)
                            )
                    )

            val result = togglService.clients()

            assertThat(result).isEqualTo(listOf(
                    TogglModel.Client(1L, 1L, "First", "First Notes"),
                    TogglModel.Client(2L, 2L, "Second, null", null)
            ))
        }
    }

    @Test
    fun testUntagBilledEntry() {
        runBlocking {

            `when`(webClient.tagId(listOf("1"), false))
                    .thenReturn(HttpStatus.OK)
            `when`(webClient.tagId(listOf("2"), false))
                    .thenReturn(HttpStatus.BAD_REQUEST)

            togglService.untagBilled(1L)
            togglService.untagBilled(2L)
        }
    }

    @Test
    fun testTagBilledEntry() {
        runBlocking {
            `when`(webClient.tagId(listOf("1"), true))
                    .thenReturn(HttpStatus.OK)
            `when`(webClient.tagId(listOf("2"), true))
                    .thenReturn(HttpStatus.BAD_REQUEST)

            togglService.untagBilled(1L)
            togglService.untagBilled(2L)
        }
    }

    @Test
    fun testUntagBilledClient() {
        runBlocking {
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 1))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                                TogglModel.TimeEntryReporting(1L, 1L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 1, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 1, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(2L, 2L, "P2", "wilma", "t2", "d2", ZonedDateTime.of(2019, 1, 2, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 2, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(3L, 3L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 3, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 3, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 2))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.TimeEntryReporting(4L, 4L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 4, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 4, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))

            `when`(webClient.tagId(listOf("1", "2", "3", "4"), false)).thenReturn(HttpStatus.OK)
            `when`(webClient.tagId(listOf("1", "2", "3", "4"), false)).thenReturn(HttpStatus.BAD_REQUEST)

            togglService.untagBilled(1L, sampleStartDate, sampleEndDate)
        }
    }

    @Test
    fun testTagBilledClient() {
        runBlocking {
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 1))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.TimeEntryReporting(1L, 1L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 1, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 1, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(2L, 2L, "P2", "wilma", "t2", "d2", ZonedDateTime.of(2019, 1, 2, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 2, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(3L, 3L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 3, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 3, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 2))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.TimeEntryReporting(4L, 4L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 4, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 4, 12, 23, 0, 0, zurichZone), 10, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))

            `when`(webClient.tagId(listOf("1", "2", "3", "4"), true)).thenReturn(HttpStatus.OK)
            `when`(webClient.tagId(listOf("1", "2", "3", "4"), true)).thenReturn(HttpStatus.BAD_REQUEST)

            togglService.tagBilled(1L, sampleStartDate, sampleEndDate)
        }
    }

    @Test
    fun testSummary() {
        runBlocking {
            `when`(webClient.summary(sampleStartDate, sampleEndDate))
                    .thenReturn(TogglModel.TogglSummary(
                            100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("fred"), 100L, listOf(TogglModel.TogglCurrency("CHF", 10.0))),
                            TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("wilma"), 200L, listOf(TogglModel.TogglCurrency("CHF", 20.0)))
                    )
                    ))

            assertThat(togglService.summary(sampleStartDate, sampleEndDate))
                    .isEqualTo(
                            TogglModel.TogglSummary(
                                    100L, 200L, listOf(
                                    TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                            ), listOf(
                                    TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("fred"), 100L, listOf(TogglModel.TogglCurrency("CHF", 10.0))),
                                    TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("wilma"), 200L, listOf(TogglModel.TogglCurrency("CHF", 20.0)))
                            )
                            )
                    )
        }
    }

    @Test
    fun testEntries() {
        runBlocking {
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 1))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.TimeEntryReporting(1L, 1L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 1, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 1, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(2L, 2L, "P2", "wilma", "t2", "d2", ZonedDateTime.of(2019, 1, 2, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 2, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                            TogglModel.TimeEntryReporting(3L, 3L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 3, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 3, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))
            `when`(webClient.entries(1L, sampleStartDate, sampleEndDate, false, 2))
                    .thenReturn(TogglModel.TogglReporting(
                            4, 3, 100L, 200L, listOf(
                            TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                    ), listOf(
                            TogglModel.TimeEntryReporting(4L, 4L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 4, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 4, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                    )
                    ))

            assertThat(togglService.entries(1L, sampleStartDate, sampleEndDate, false))
                    .isEqualTo(
                            TogglModel.TogglReporting(
                                    4, 3, 100, 200, listOf(
                                    TogglModel.TogglCurrency("CHF", 10.0), TogglModel.TogglCurrency("EUR", 20.0)
                            ), listOf(
                                    TogglModel.TimeEntryReporting(1L, 1L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 1, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 1, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                                    TogglModel.TimeEntryReporting(3L, 3L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 3, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 3, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                                    TogglModel.TimeEntryReporting(4L, 4L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 4, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 4, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                                    TogglModel.TimeEntryReporting(2L, 2L, "P2", "wilma", "t2", "d2", ZonedDateTime.of(2019, 1, 2, 12, 13, 0, 0, zurichZone), ZonedDateTime.of(2019, 1, 2, 12, 23, 0, 0, zurichZone), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2"))
                            )
                            )
                    )
        }
    }
}
