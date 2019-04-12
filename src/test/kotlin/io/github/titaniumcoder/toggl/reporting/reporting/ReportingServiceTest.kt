package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalDateTime


// TODO find a better way than just run it inside a runBlock for everything
@ExtendWith(SpringExtension::class)
class ReportingServiceTest {
    @TestConfiguration
    class TogglClientTestConfiguration {
        @Bean
        fun reportingService(): ReportingService =
                ReportingService()
    }

    @Autowired
    private lateinit var reportingService: ReportingService

    private val sampleStartDate: LocalDate = LocalDate.of(2019, 2, 10)
    private val sampleEndDate: LocalDate = LocalDate.of(2019, 10, 15)

    @Test
    fun testExcelCration() {
        val excel = reportingService.generateExcel(ViewModel.ReportingModel(
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
        )))

        // read in the excel to make sure it's still readable and has some general info inside
        val workbook: Workbook = XSSFWorkbookFactory.create(excel.inputStream())
        assertThat(workbook.numberOfSheets).isEqualTo(1)

        // TODO write more tests...
    }
}
