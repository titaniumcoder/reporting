package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import io.github.titaniumcoder.toggl.reporting.transformers.TransformerService
import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class ReportingController(val service: ReportingService, val client: TogglClient, val transformer: TransformerService) {
    @GetMapping("/api/client/{clientId}")
    fun entries(clientId: Long, @RequestParam(required = false) from: LocalDate?, @RequestParam(required = false) to: LocalDate?, @RequestParam(required = false, defaultValue = "false") tagged: Boolean): ViewModel.ReportingModel {
        val definiteTo = to ?: (LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1))
        val definiteFrom = from ?: (definiteTo.withDayOfMonth(1))

        val entries = client.entries(clientId, definiteFrom, definiteTo, tagged)
        return transformer.transformInput(entries, definiteFrom, definiteTo, clientId)
    }

    @GetMapping("/api/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])

    /*
      .withHeaders(CONTENT_DISPOSITION -> s"attachment; filename=${excelModel.name}-${datum.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
     */
    fun timesheet(clientId: Long, @RequestParam(required = true) from: LocalDate, @RequestParam(required = true) to: LocalDate): ResponseEntity<ByteArray> {
        val entries = client.entries(clientId, from, to, true)
        val excel = service.generateExcel(transformer.transformInput(entries, from, to, clientId))

        val name = entries.data.firstOrNull()?.client?.toLowerCase() ?: "unbekannt"

        return ResponseEntity.ok().header("Conent-Disposition", "attachment; filename=$name-${from.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
                .body(excel)
    }
}