package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.toggl.TogglService
import io.github.titaniumcoder.toggl.reporting.transformers.TransformerService
import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import org.springframework.stereotype.Service
import java.time.LocalDate

class ExcelSheet(val name: String, val date: LocalDate, val excel: ByteArray)

@Service
class ReportingService(val service: TogglService, val transformer: TransformerService) {
    private fun generateExcel(name: String, model: ViewModel.ReportingModel): ByteArray {
        fun tableheader(cell: Cell) {
            cell.apply {
                bold = true
                top = 1
                bottom = 1
                left = 1
                right = 1
            }
        }
        return excel {
            sheet(name) {
                // ---- header ----
                cell(0, 0) {
                    tableheader(this)
                    size = 14
                    mergedCols = 5
                    format = "\"Arbeitszeit \"MMMM yyyy"
                    content = model.timeEntries.flatMap { te -> te.map { it.startdate } }.first()
                }
                cell(0, 5) {
                    tableheader(this)
                    size = 14
                    alignment = HorizontalAlignment.Right
                    mergedCols = 2
                    content = "Rico Metzger" // FIXME move this to environment properties
                }

                cell(2, 0) {
                    tableheader(this)

                    content = "Datum"
                }
                cell(2, 1) {
                    tableheader(this)
                    alignment = HorizontalAlignment.Right
                    content = "Summe Tag"
                }
                cell(2, 2) {
                    tableheader(this)
                    alignment = HorizontalAlignment.Center
                    content = "Von"
                }
                cell(2, 3) {
                    tableheader(this)
                    alignment = HorizontalAlignment.Center
                    content = "Bis"
                }
                cell(2, 4) {
                    tableheader(this)
                    alignment = HorizontalAlignment.Right
                    content = "Zeit"
                }
                cell(2, 5) {
                    tableheader(this)
                    content = "Projekt"
                }
                cell(2, 6) {
                    tableheader(this)
                    content = "Bemerkung"
                }

                var row = 3

                // ---- Time Entries ----
                model.timeEntries.forEach { te ->
                    val sum = te.sumBy { it.minutes }

                    te.forEachIndexed { idx, entry ->
                        val firstRowInDay = idx == 0
                        val lastRowInDay = idx == te.size - 1

                        val topBorder = if (firstRowInDay) 1 else 2
                        val bottomBorder = if (lastRowInDay) 1 else 2

                        if (firstRowInDay) {
                            cell(row, 0) {
                                content = entry.day
                                format = "dd.MM.yyyy"
                                top = topBorder
                                bottom = 1
                                mergedRows = te.size
                            }
                            cell(row, 1) {
                                content = formatTime(sum)
                                format = "[h]:mm"
                                alignment = HorizontalAlignment.Right
                                top = topBorder
                                bottom = 1
                                mergedRows = te.size
                            }
                        }

                        cell(row, 2) {
                            content = entry.startdate
                            format = "hh:mm"
                            alignment = HorizontalAlignment.Center

                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 3) {
                            content = entry.enddate
                            format = "hh:mm"
                            alignment = HorizontalAlignment.Center

                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 4) {
                            content = formatTime(entry.minutes)
                            format = "[h]:mm"
                            alignment = HorizontalAlignment.Right

                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 5) {
                            content = entry.project

                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 6) {
                            content = entry.description

                            top = topBorder
                            bottom = bottomBorder
                        }

                        row += 1
                    }
                }

                // ---- Footer ----
                row += 1

                val total = model.timeEntries.sumBy { it.sumBy { d -> d.minutes } }
                val totalPerProject = model
                        .timeEntries
                        .flatten()
                        .groupBy { it.project ?: "<<< Kein Projekt >>>" }
                        .map { entry -> entry.key to entry.value.sumBy { it.minutes } }
                        .toMap()

                cell(row, 0) {
                    content = "Summen:"
                    mergedCols = 3
                    bold = true
                }
                cell(row, 3) {
                    content = "(Stunden):"
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row, 4) {
                    content = "(dezimal):"
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 1, 0) {
                    content = "Total:"
                    mergedCols = 3
                    bold = true
                }
                cell(row + 1, 3) {
                    content = formatTime(total)
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 1, 4) {
                    content = formatNumeric(total)
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 3, 0) {
                    content = "Pro Projekt:"
                    mergedCols = 5
                    alignment = HorizontalAlignment.Center
                    bold = true

                    top = 1
                    left = 1
                    right = 1
                }

                totalPerProject
                        .toList()
                        .sortedBy { it.first }
                        .forEachIndexed { idx, e ->
                            cell(row + 4 + idx, 0) {
                                content = e.first
                                mergedCols = 3

                                top = null
                                left = 1
                                right = 1
                                bottom = if (idx == totalPerProject.size - 1) 1 else null
                            }
                            cell(row + 4 + idx, 3) {
                                content = formatTime(e.second)
                                format = "[h]:mm"

                                top = null
                                left = 1
                                right = null
                                bottom = if (idx == totalPerProject.size - 1) 1 else null
                            }
                            cell(row + 4 + idx, 4) {
                                content = formatNumeric(e.second)
                                format = "0.00"

                                top = null
                                left = null
                                right = 1
                                bottom = if (idx == totalPerProject.size - 1) 1 else null
                            }
                        }

            }
        }
                .render()
    }


    suspend fun timesheet(clientId: Long, from: LocalDate, to: LocalDate): ExcelSheet {
        val entries = service.entries(clientId, from, to, true)

        val name = entries.data.firstOrNull()?.client?.toLowerCase() ?: "unbekannt"

        val body = generateExcel(name, transformer.transformInput(entries, from, to, clientId))

        return ExcelSheet(name, from, body)
    }

    suspend fun entries(clientId: Long, from: LocalDate?, to: LocalDate?, tagged: Boolean): ViewModel.ReportingModel {
        val definiteTo = to ?: (LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1))
        val definiteFrom = from ?: (definiteTo.withDayOfMonth(1))

        val entries = service.entries(clientId, definiteFrom, definiteTo, tagged)
        return transformer.transformInput(entries, definiteFrom, definiteTo, clientId)
    }

    private fun formatTime(minutes: Int): Double = minutes.toDouble() / 1440

    private fun formatNumeric(minutes: Int): Double = minutes.toDouble() / 60
}