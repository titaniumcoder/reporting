package io.github.titaniumcoder.toggl.reporting.reporting;

import io.github.titaniumcoder.toggl.reporting.transformers.ReportingModel;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;

import java.time.LocalDate;

@Controller("/api")
public class ReportingController {
    private final ReportingService service;

    public ReportingController(ReportingService service) {
        this.service = service;
    }

    @Get("/client/{clientId}")
    @Secured("isAuthenticated()")
    public ReportingModel entries(@PathVariable long clientId,
                                  /* @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) */ LocalDate from,
                                  /* @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) */ LocalDate to) {
        return service.entries(clientId, from, to);
    }

    @Get("/timesheet/{clientId}")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Secured("isAuthenticated()")
    public byte[] timesheet(
            @PathVariable long clientId,
            /* @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) */ LocalDate from,
            /* @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) */ LocalDate to
    ) {
        var sheet = service.timesheet(clientId, from, to);

        return new byte[0];
        /* TODO:
        var h = HttpHeaders()
        h.contentDisposition = ContentDisposition.builder("attachment")
                .filename("${sheet.name}-${sheet.date.format(DateTimeFormatter.ofPattern("MM-yyyy"))}.xlsx")
                .size(sheet.excel.size.toLong())
                .build()

        return ResponseEntity
                .ok()
                .headers(h)
                .body(sheet.excel)
         */
    }
}
