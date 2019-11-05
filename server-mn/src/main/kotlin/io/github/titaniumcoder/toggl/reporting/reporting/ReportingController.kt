package io.github.titaniumcoder.toggl.reporting.reporting

import java.time.LocalDate

//@RestController
//@RequestMapping("/api")
class ReportingController(val service: ReportingService) {
    //    @GetMapping("/client/{clientId}")
//    @Secured("isAuthenticated()")
    suspend fun entries(
            /*@PathVariable */ clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate?,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate?
    ) =
            service.entries(clientId, from, to)

    //    @GetMapping(
//            value = ["/timesheet/{clientId}"],
//            produces = ["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
//    )
//    @Secured("isAuthenticated()")
    suspend fun timesheet(
            /* @PathVariable */ clientId: Long,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("from") */ from: LocalDate,
            /* @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("to") */ to: LocalDate
    ): ByteArray = TODO()
//    ResponseEntity<ByteArray> {
//        val sheet = service.timesheet(clientId, from, to)
//
//        val filename = "${sheet.name.toUpperCase()}-${sheet.date.format(DateTimeFormatter.ofPattern("yyyy-MM"))}.xlsx"
//
//        return ResponseEntity
//                .ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
//                .body(sheet.excel)
//    }
}
