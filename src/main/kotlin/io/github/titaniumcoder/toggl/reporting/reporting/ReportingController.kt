package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import io.github.titaniumcoder.toggl.reporting.transformers.TransformerService
import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
class ReportingController(val service: ReportingService, val client: TogglClient, val transformer: TransformerService) {
    @GetMapping("/api/client/{clientId}")
    fun entries(
            @PathVariable clientId: Long,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?,
            @RequestParam(required = false, defaultValue = "false") tagged: Boolean
    ): Mono<ViewModel.ReportingModel> {
        val definiteTo = to ?: (LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1))
        val definiteFrom = from ?: (definiteTo.withDayOfMonth(1))

        val entries = client.entries(clientId, definiteFrom, definiteTo, tagged)
        return entries.map { transformer.transformInput(it, definiteFrom, definiteTo, clientId) }
    }

    @GetMapping("/api/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun timesheet(
            @PathVariable clientId: Long,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ): Mono<ResponseEntity<ByteArray>> {
        val entries = client.entries(clientId, from, to, true)

        val body = entries.map { service.generateExcel(transformer.transformInput(it, from, to, clientId)) }

        val name = entries.map { it.data.firstOrNull()?.client?.toLowerCase() ?: "unbekannt" }

        return body.zipWith(name).map { zipped ->
            ResponseEntity.ok().header("Conent-Disposition", "attachment; filename=${zipped.t2}-${from.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
                    .body(zipped.t1)
        }
    }
}
