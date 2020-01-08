package io.github.titaniumcoder.reporting.reporting

import io.github.titaniumcoder.reporting.timeentry.TimeEntryDto
import io.github.titaniumcoder.reporting.user.UserService
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.time.LocalDate
import java.time.LocalDateTime

class ExcelSheet(val name: String, val date: LocalDate, @Suppress("unused") val excel: ByteArray)

@Service
class ReportingService(val client: DatabaseClient, val userService: UserService) {
    @Suppress("unused", "UNUSED_PARAMETER")
    private fun generateExcel(name: String, clientInfo: ClientInfo): ByteArray {
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
                    size = 14
                    bold = true
                    mergedCols = 5
                    alignment = HorizontalAlignment.Left

                    format = "\"Arbeitszeit \"MMMM yyyy"
                    content = "2019-05-05" // TODO: model.timeEntries.flatMap { te -> te.map { /* TODO it.startdate */ LocalDate.now() } }.last()
                }

                cell(0, 5) {
                    size = 14
                    bold = true
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
                // TODO:
                run {
                    val te: List<TimeEntryDto> = listOf(TimeEntryDto(null, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now(), null, "ABC", "DEF", "GHI", true, false, 100, 1000.0))

                    val sum = te.sumBy { it.timeUsed?.toInt() ?: 0 }

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
                                content = formatTime(sum)
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
                            content = formatTime(entry.timeUsed?.toInt() ?: 0)
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
                row += 1

                val total = 1000 // TODO model.timeEntries.sumBy { it.sumBy { d -> d.minutes } }
                val totalPerProject = mapOf(Pair("ref", 100))

                /* model
                .timeEntries
                .flatten()
                .groupBy { it.project ?: "<<< Kein Projekt >>>" }
                .map { entry -> entry.key to entry.value.sumBy { it.minutes } }
                .toMap()
                 */

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
                    format = "[h]:mm"
                    bold = true
                    alignment = HorizontalAlignment.Right
                }
                cell(row + 1, 4) {
                    content = formatNumeric(total)
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


    @Suppress("unused")
    fun timesheet(clientId: String, from: LocalDate, to: LocalDate): ExcelSheet {
        val clientInfo = info(clientId, from, to)
                .blockFirst()!! // TODO replace this

        val name = clientInfo.name

        val body = generateExcel(name, clientInfo)

        return ExcelSheet(name, from, body)
    }

    @Suppress("unused")
    private fun formatTime(minutes: Int): Double = minutes.toDouble() / 1440

    @Suppress("unused")
    private fun formatNumeric(minutes: Int): Double = minutes.toDouble() / 60

    fun info(clientId: String?, from: LocalDate?, to: LocalDate?): Flux<ClientInfo> {
        return userService.reactiveCurrentUserDto()
                .flatMapMany { user ->
                    val canSeeMoney = user.admin || user.canViewMoney

                    client.execute("select * from client_overview " + (if (clientId != null) "where client_id = '$clientId'" else ""))
                            .map { row ->
                                ClientInfo(
                                        id = row.get("client_id", String::class.java) ?: "<<ID>>",
                                        name = row.get("client_name", String::class.java) ?: "<<<< NAME >>>>>",
                                        maxMinutes = row.get("client_max_minutes", Integer::class.java)?.toInt(),
                                        rateInCentsPerHour = row.get("client_rate_in_cents_per_hour", Integer::class.java)?.toInt(),
                                        billedAmount = null,
                                        billedMinutes = 0,
                                        openAmount = null,
                                        openMinutes = 0,
                                        remainingAmount = null,
                                        remainingMinutes = null,
                                        projects = listOf(
                                                ProjectInfo(
                                                        projectId = row.get("project_id", java.lang.Long::class.java)?.toLong(),
                                                        name = row.get("project_name", String::class.java)
                                                                ?: "<<< NAME >>>>",
                                                        maxMinutes = row.get("project_max_minutes", Integer::class.java)?.toInt(),
                                                        billable = row.get("project_billable", java.lang.Boolean::class.java)?.booleanValue()
                                                                ?: false,
                                                        rateInCentsPerHour = row.get("project_rate_in_cents_per_hour", Integer::class.java)?.toInt(),
                                                        billedMinutes = row.get("project_billed_minutes", Integer::class.java)?.toInt()
                                                                ?: 0,
                                                        billedAmount = null,
                                                        openMinutes = row.get("project_open_minutes", Integer::class.java)?.toInt()
                                                                ?: 0,
                                                        openAmount = null,
                                                        remainingMinutes = null,
                                                        remainingAmount = null
                                                )
                                        )
                                )
                            }
                            .all()
                            .collectList()
                            .flatMapMany { clientInfos ->
                                Flux.fromIterable(
                                        clientInfos
                                                .groupBy { it.id }
                                                .map { e ->
                                                    val client = e.value.first()
                                                    val projects =
                                                            e.value
                                                                    .flatMap { it.projects }

                                                    val imProjects =
                                                            projects.map { project ->
                                                                project.copy(
                                                                        remainingMinutes = project.maxMinutes?.let { Math.max(it - project.billedMinutes - project.openMinutes, 0) }
                                                                )
                                                            }

                                                    val cProjects =
                                                            if (canSeeMoney) {
                                                                imProjects
                                                                        .map { project ->
                                                                            val rateN = if (project.billable) project.rateInCentsPerHour?.let { if (it == 0) null else it }
                                                                                    ?: client.rateInCentsPerHour else null
                                                                            project.copy(
                                                                                    rateInCentsPerHour = rateN,
                                                                                    billedAmount = rateN?.let { rate -> project.billedMinutes * rate / 60.0 },
                                                                                    openAmount = rateN?.let { rate -> project.openMinutes * rate / 60.0 },
                                                                                    remainingAmount = rateN?.let { rate -> project.remainingMinutes?.let { it * rate / 60.0 } }
                                                                            )
                                                                        }
                                                            } else
                                                                projects


                                                    val imClient = client.copy(
                                                            rateInCentsPerHour = if (canSeeMoney) client.rateInCentsPerHour else null,
                                                            projects = cProjects.sortedBy { it.name }
                                                    )

                                                    val imClient2 = imClient.copy(
                                                            billedMinutes = imClient.projects.filter { it.billable }.sumBy { it.billedMinutes },
                                                            openMinutes = imClient.projects.filter { it.billable }.sumBy { it.openMinutes }
                                                    )

                                                    val remainingMinutes = imClient2.maxMinutes?.let { mm -> mm - imClient2.billedMinutes - imClient2.openMinutes }

                                                    val imClient3 = imClient2.copy(
                                                            remainingMinutes = remainingMinutes,

                                                            billedAmount = imClient2.rateInCentsPerHour?.let { rate -> rate * imClient2.billedMinutes / 60.0 },
                                                            openAmount = imClient.rateInCentsPerHour?.let { rate -> rate * imClient2.openMinutes / 60.0 },
                                                            remainingAmount = imClient.rateInCentsPerHour?.let { rate -> remainingMinutes?.let { rate * it / 60.0 } }
                                                    )

                                                    if (clientId == null) {
                                                        imClient3.copy(projects = listOf())
                                                    } else {
                                                        imClient3
                                                    }
                                                }
                                )
                            }
                }
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
        val rateInCentsPerHour: Int? = null,
        val maxMinutes: Int? = null,
        val billedMinutes: Int,
        val billedAmount: Double? = null,
        val openMinutes: Int,
        val openAmount: Double? = null,
        val remainingMinutes: Int? = null,
        val remainingAmount: Double? = null
)
