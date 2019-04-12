package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.toggl.TogglClient
import io.github.titaniumcoder.toggl.reporting.transformers.TransformerService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
    ) = GlobalScope.mono {
        // TODO alternatively we could try to use suspend
        val definiteTo = to ?: (LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1))
        val definiteFrom = from ?: (definiteTo.withDayOfMonth(1))

        val entries = client.entries(clientId, definiteFrom, definiteTo, tagged)
        transformer.transformInput(entries, definiteFrom, definiteTo, clientId)
    }

    @GetMapping("/api/timesheet/{clientId}", produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"])
    fun timesheet(
            @PathVariable clientId: Long,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
            @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate
    ) = GlobalScope.mono {
        val entries = client.entries(clientId, from, to, true)

        val body = service.generateExcel(transformer.transformInput(entries, from, to, clientId))

        val name = entries.data.firstOrNull()?.client?.toLowerCase() ?: "unbekannt"

        ResponseEntity
                .ok()
                .header("Conent-Disposition", "attachment; filename=$name-${from.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
                .body(body)
    }
}
