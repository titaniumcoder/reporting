package io.github.titaniumcoder.toggl.reporting.toggl

import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class TogglController(val client: TogglClient) {
    @GetMapping("/api/clients")
    fun clients() = client.clients()

    @PutMapping("/api/client/{clientId}/billed")
    fun tagBilled(@PathVariable clientId: Long, @RequestParam from: LocalDate, @RequestParam to: LocalDate) {
        client.tagBilled(clientId, from, to)
    }

    @DeleteMapping("/api/client/{clientId}/billed")
    fun untagBilled(@PathVariable clientId: Long, @RequestParam from: LocalDate, @RequestParam to: LocalDate) {
        client.untagBilled(clientId, from, to)
    }

    @PutMapping("/api/tag/{entry}")
    fun tagEntry(@PathVariable entry: Long) {
        client.tagBilled(entry)
    }

    @DeleteMapping("/api/tag/{entry}")
    fun untagEntry(@PathVariable entry: Long) {
        client.tagBilled(entry)
    }
}