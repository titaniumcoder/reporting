package io.github.titaniumcoder.reporting.reporting

import io.github.titaniumcoder.reporting.client.ClientService
import io.github.titaniumcoder.reporting.model.Client
import io.github.titaniumcoder.reporting.timeentry.TimeEntryDto
import io.github.titaniumcoder.reporting.timeentry.TimeEntryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton
import kotlin.math.floor

class ExcelSheet(
        val name: String,
        val date: LocalDate,
        val excel: ByteArray
)

@Singleton
class ReportingService(
        val timeEntryService: TimeEntryService,
        val clientService: ClientService
) {
    suspend fun timesheet(clientId: String, billableOnly: Boolean): ExcelSheet? {
        return clientService
                .findById(clientId)
                ?.let { c ->
                    val t = timeEntryService.retrieveTimeEntries(null, null, clientId, false, billableOnly)
                    if (t.isEmpty()) {
                        null
                    } else {
                        val sortedList = t.sortedBy { it.starting }.toList()
                        val body = generateExcel(c, sortedList)

                        ExcelSheet(name = c.id, date = sortedList.first().date, excel = body)
                    }
                }
    }

    private fun calcMinutes(start: LocalDateTime, end: LocalDateTime): Int {
        val dur = Duration.between(start, end)

        return floor(dur.toSeconds().toDouble() / 60).toInt()
    }

    suspend fun info(clientId: String?, from: LocalDate?, to: LocalDate?): Flow<ClientInfo> =
            clientService.clients()
                    .map { client ->
                        val rate = client.rateInCentsPerHour ?: 0

                        val projects = client
                                .projects
                                .map { project ->
                                    val billed = project.timeEntries.filter { it.billed }
                                    val open = project.timeEntries.filterNot { it.billed }

                                    val billedMinutes = billed.map { calcMinutes(it.starting, it.ending) }.sum()
                                    val openMinutes = open.map { calcMinutes(it.starting, it.ending) }.sum()

                                    val billedAmount = billedMinutes.toDouble() * rate / 60 / 100
                                    val openAmount = openMinutes.toDouble() * rate / 60 / 100

                                    ProjectInfo(
                                            projectId = null,
                                            name = project.name,
                                            billable = project.billable,
                                            billedMinutes = billedMinutes,
                                            billedAmount = billedAmount,
                                            openMinutes = openMinutes,
                                            openAmount = openAmount
                                    )
                                }

                        val openMinutes = projects.map { it.openMinutes }.sum()
                        val billedMinutes = projects.map { it.billedMinutes }.sum()
                        val remainingMinutes = client.maxMinutes?.let { it - openMinutes - billedMinutes }
                        val remainingAmount = remainingMinutes?.let { it.toDouble() * rate / 60 / 100 }

                        ClientInfo(
                                id = client.id,
                                name = client.name,
                                rateInCentsPerHour = client.rateInCentsPerHour,
                                maxMinutes = client.maxMinutes,
                                billedMinutes = billedMinutes,
                                billedAmount = projects.map { it.billedAmount }.sum(),
                                openMinutes = openMinutes,
                                openAmount = projects.map { it.openAmount }.sum(),
                                remainingMinutes = remainingMinutes,
                                remainingAmount = remainingAmount,
                                projects = projects
                        )
                    }

    private fun generateExcel(client: Client, timeEntries: List<TimeEntryDto>): ByteArray {
        fun tableheader(cell: Cell) {
            cell.apply {
                bold = true
                top = 1
                bottom = 1
                left = 1
                right = 1
            }
        }

        // TODO make this generation more flexibel
        return excel {
            sheet(client.name) {
                // ---- header ----
                cell(0, 0) {
                    size = 14
                    bold = true
                    mergedCols = 5
                    alignment = HorizontalAlignment.Left

                    format = "\"Arbeitszeit \"MMMM yyyy"
                    content = timeEntries.first().date
                }

                cell(0, 5) {
                    size = 14
                    bold = true
                    alignment = HorizontalAlignment.Right
                    mergedCols = 2
                    content = "Rico Metzger"
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

                val firstRow = 3

                // ---- Time Entries ----
                // TODO:
                timeEntries.groupBy { it.date }.forEach { (_, te) ->
                    val rowCount = te.size

                    te.forEachIndexed { idx, entry ->
                        val firstRowInDay = idx == 0
                        val lastRowInDay = idx == te.size - 1

                        val topBorder = if (firstRowInDay) 1 else 2
                        val bottomBorder = if (lastRowInDay) 1 else 2

                        if (firstRowInDay) {
                            cell(row, 0) {
                                content = entry.date
                                format = "dd.MM.yyyy"
                                top = topBorder
                                left = 1
                                right = 1
                                bottom = 1
                                mergedRows = te.size
                            }
                            cell(row, 1) {
                                content = ExcelFormula("SUM(E${row + 1}:E${row + rowCount})")
                                format = "[h]:mm"
                                alignment = HorizontalAlignment.Right
                                top = topBorder
                                left = 1
                                right = 1
                                bottom = 1
                                mergedRows = te.size
                            }
                        }

                        cell(row, 2) {
                            content = entry.starting
                            format = "hh:mm"
                            alignment = HorizontalAlignment.Center

                            left = 1
                            right = 1
                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 3) {
                            content = entry.ending
                            format = "hh:mm"
                            alignment = HorizontalAlignment.Center

                            left = 1
                            right = 1
                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 4) {
                            content = ExcelFormula("D${row + 1}-C${row + 1}")
                            format = "[h]:mm"
                            alignment = HorizontalAlignment.Right

                            left = 1
                            right = 1
                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 5) {
                            content = entry.projectName

                            left = 1
                            right = 1
                            top = topBorder
                            bottom = bottomBorder
                        }
                        cell(row, 6) {
                            content = entry.description

                            left = 1
                            right = 1
                            top = topBorder
                            bottom = bottomBorder
                        }

                        row += 1
                    }
                }

                // ---- Footer ----
                val lastRow = row

                row += 1

                val totalPerProject = timeEntries
                        .groupBy { it.projectName ?: "<<< Kein Projekt >>>" }
                        .map { entry -> entry.key }
                        .toList()
                        .sortedBy { it }

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
                    content = ExcelFormula("SUM(E${firstRow + 1}:E$lastRow)")
                    format = "[h]:mm"
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 1, 4) {
                    content = ExcelFormula("D${row + 2}*24")
                    format = "0.00"
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 3, 0) {
                    content = "Pro Projekt:"
                    mergedCols = 3
                    alignment = HorizontalAlignment.Center
                    bold = true

                    top = 1
                    left = 1
                    right = 1
                }
                cell(row + 3, 3) {
                    content = ""
                    mergedCols = 2
                    alignment = HorizontalAlignment.Center

                    top = 1
                    left = 1
                    right = 1
                }

                totalPerProject
                        .toList()
                        .forEachIndexed { idx, e ->
                            cell(row + 4 + idx, 0) {
                                content = e
                                mergedCols = 3

                                top = null
                                left = 1
                                right = 1
                                bottom = if (idx == totalPerProject.size - 1) 1 else null
                            }
                            cell(row + 4 + idx, 3) {
                                content = ExcelFormula(
                                        "SUMIF(F${firstRow + 1}:F$lastRow," +
                                                "\$A\$${row + idx + 5}," +
                                                "E${firstRow + 1}:E$lastRow)")
                                format = "[h]:mm"

                                top = null
                                left = 1
                                right = null
                                bottom = if (idx == totalPerProject.size - 1) 1 else null
                            }
                            cell(row + 4 + idx, 4) {
                                content = ExcelFormula("D${row + 5 + idx}*24")
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
}

data class ClientInfo(
        val id: String,
        val name: String,
        val rateInCentsPerHour: Int? = null,
        val maxMinutes: Int? = null,
        val billedMinutes: Int,
        val billedAmount: Double? = null,
        val openMinutes: Int,
        val openAmount: Double? = null,
        val remainingMinutes: Int? = null,
        val remainingAmount: Double? = null,
        val projects: List<ProjectInfo>
)

data class ProjectInfo(
        val projectId: Long? = null,
        val name: String,
        val billable: Boolean,
        val billedMinutes: Int,
        val billedAmount: Double,
        val openMinutes: Int,
        val openAmount: Double
)
