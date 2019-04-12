package io.github.titaniumcoder.toggl.reporting.transformers

import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


// TODO find a better way than just run it inside a runBlock for everything
@ExtendWith(SpringExtension::class)
class TransformerServiceTest {
    @TestConfiguration
    class TogglClientTestConfiguration {
        @Bean
        fun transformerService(): TransformerService =
                TransformerService()
    }

    @Autowired
    private lateinit var transformerService: TransformerService

    private val sampleStartDate: LocalDate = LocalDate.of(2019, 2, 10)
    private val sampleEndDate: LocalDate = LocalDate.of(2019, 10, 15)

    @Test
    fun testCashoutCalculation() {
        assertThat(transformerService.cash(TogglModel.TogglSummary(
                100L, 200L, listOf(), listOf(
                TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("c1"), 100, listOf(TogglModel.TogglCurrency("CHF", 100.50))),
                TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("c1"), 100, listOf(TogglModel.TogglCurrency("CHF", 99.50))),
                TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("c3"), 100, listOf(TogglModel.TogglCurrency("CHF", 101.50))),
                TogglModel.ClientSummaryEntry(TogglModel.TogglIClient("c2"), 100, listOf(TogglModel.TogglCurrency("CHF", 102.50)))
        )
        )
        ))
                .isEqualTo(listOf(
                        ViewModel.Cashout("c1", 200.00),
                        ViewModel.Cashout("c2", 102.50),
                        ViewModel.Cashout("c3", 101.50)
                ))
    }

    @Test
    fun testTransformer() {
        assertThat(transformerService.transformInput(TogglModel.TogglReporting(
                4, 3, 100L, 200L, listOf(), listOf(
                TogglModel.TimeEntryReporting(1L, 1L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 1, 12, 13, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2019, 1, 1, 12, 23, 0, 0, ZoneId.systemDefault()), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                TogglModel.TimeEntryReporting(3L, 3L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 3, 12, 13, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2019, 1, 3, 12, 23, 0, 0, ZoneId.systemDefault()), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                TogglModel.TimeEntryReporting(4L, 4L, "P1", "fred", "t1", "d1", ZonedDateTime.of(2019, 1, 4, 12, 13, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2019, 1, 4, 12, 23, 0, 0, ZoneId.systemDefault()), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2")),
                TogglModel.TimeEntryReporting(2L, 2L, "P0", "fred", "t2", "d2", ZonedDateTime.of(2019, 1, 2, 12, 13, 0, 0, ZoneId.systemDefault()), ZonedDateTime.of(2019, 1, 2, 12, 23, 0, 0, ZoneId.systemDefault()), 600_000, 10.0f, true, "CHF", listOf("tag1", "tag2"))
        )
        ), sampleStartDate, sampleEndDate, 1L
        ))
                .isEqualTo(
                        ViewModel.ReportingModel(
                                "fred", 1L, sampleStartDate, sampleEndDate, listOf(
                                ViewModel.Project("P0", 10), ViewModel.Project("P1", 30)
                        ), listOf(
                                listOf(
                                        ViewModel.TimeEntry(1L, LocalDate.of(2019, 1, 1), "P1", LocalDateTime.of(2019, 1, 1, 12, 13, 0, 0), LocalDateTime.of(2019, 1, 1, 12, 23, 0, 0), 10, "d1", listOf("tag1", "tag2"))
                                ),
                                listOf(
                                        ViewModel.TimeEntry(2L, LocalDate.of(2019, 1, 2), "P0", LocalDateTime.of(2019, 1, 2, 12, 13, 0, 0), LocalDateTime.of(2019, 1, 2, 12, 23, 0, 0), 10, "d2", listOf("tag1", "tag2"))
                                ),
                                listOf(
                                        ViewModel.TimeEntry(3L, LocalDate.of(2019, 1, 3), "P1", LocalDateTime.of(2019, 1, 3, 12, 13, 0, 0), LocalDateTime.of(2019, 1, 3, 12, 23, 0, 0), 10, "d1", listOf("tag1", "tag2"))
                                ),
                                listOf(
                                        ViewModel.TimeEntry(4L, LocalDate.of(2019, 1, 4), "P1", LocalDateTime.of(2019, 1, 4, 12, 13, 0, 0), LocalDateTime.of(2019, 1, 4, 12, 23, 0, 0), 10, "d1", listOf("tag1", "tag2"))
                                )
                        ))
                )
    }
}
