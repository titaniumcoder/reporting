package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import io.github.titaniumcoder.toggl.reporting.toggl.TogglModel
import io.github.titaniumcoder.toggl.reporting.transformers.TransformerService
import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.ZonedDateTime


// TODO find a better way than just run it inside a runBlock for everything
@ExtendWith(SpringExtension::class)
class ReportingServiceTest {
    @TestConfiguration
    class TogglClientTestConfiguration {
        @Bean
        fun reportingService(service: TogglService, transformer: TransformerService): ReportingService =
                ReportingService(service, transformer)
    }

    @Autowired
    private lateinit var reportingService: ReportingService

    @MockBean
    private lateinit var service: TogglService

    @MockBean
    private lateinit var transformer: TransformerService

    private val sampleStartDate: LocalDate = LocalDate.of(2019, 2, 10)
    private val sampleEndDate: LocalDate = LocalDate.of(2019, 10, 15)

    private val start = ZonedDateTime.now().minusMinutes(1)
    private val end = ZonedDateTime.now()
    private val te = TogglModel.TogglReporting(
            1, 1, null, null, listOf(), listOf(
            TogglModel.TimeEntryReporting(1, 1, "a", "a", "a", "d", start, end, 60_000, 10.0f, true, "CHF", listOf("a", "b"))
    ))
    private val rm = ViewModel.ReportingModel(
            "a", 1L, sampleStartDate, sampleEndDate, listOf(ViewModel.Project("a", 1)), listOf(
            listOf(
                    ViewModel.TimeEntry(1, start.toLocalDate(), "a", start, end, 1, "d", listOf("a", "b"))
            )
    )
    )

    @BeforeEach
    fun setupMocks() {
    }

    @Test
    fun testTimesheetCreation() {
        runBlocking {
            `when`(service.entries(1L, sampleStartDate, sampleEndDate, true))
                    .thenReturn(te)
            `when`(transformer.transformInput(te, sampleStartDate, sampleEndDate, 1L))
                    .thenReturn(rm)

            val excel = reportingService.timesheet(1L, sampleStartDate, sampleEndDate)

            // read in the excel to make sure it's still readable and has some general info inside
            val workbook: Workbook = withContext(Dispatchers.IO) { XSSFWorkbookFactory.create(excel.excel.inputStream()) }
            assertThat(workbook.numberOfSheets).isEqualTo(1)

            // TODO write more tests...
        }
    }

    @Test
    fun testEntriesRetrieval() {
        runBlocking {
            `when`(service.entries(1L, sampleStartDate, sampleEndDate, false))
                    .thenReturn(te)
            `when`(transformer.transformInput(te, sampleStartDate, sampleEndDate, 1L))
                    .thenReturn(rm)

            val entries = reportingService.entries(1L, sampleStartDate, sampleEndDate, false)

            // read in the excel to make sure it's still readable and has some general info inside
            assertThat(entries)
                    .isEqualTo(
                            ViewModel.ReportingModel(
                                    "a", 1L, sampleStartDate, sampleEndDate, listOf(
                                    ViewModel.Project("a", 1)
                            ), listOf(
                                    listOf(
                                            ViewModel.TimeEntry(1, start.toLocalDate(), "a", start, end, 1, "d", listOf("a", "b"))
                                    )
                            ))
                    )
        }
    }
}
