package io.github.titaniumcoder.reporting.reporting

import io.github.titaniumcoder.reporting.client.Client
import io.github.titaniumcoder.reporting.client.ClientService
import io.github.titaniumcoder.reporting.timeentry.TimeEntryDto
import io.github.titaniumcoder.reporting.timeentry.TimeEntryService
import io.github.titaniumcoder.reporting.user.UserService
import java.time.LocalDate
import javax.inject.Singleton
import kotlin.math.max

class ExcelSheet(
        val name: String,
        val date: LocalDate,
        val excel: ByteArray
)

@Singleton
class ReportingService(
        val userService: UserService,
        val timeEntryService: TimeEntryService,
        val clientService: ClientService
) {
    fun timesheet(clientId: String, billableOnly: Boolean): ExcelSheet? {
        return clientService
                .findById(clientId)
                ?.let { c ->
                    val t = timeEntryService.retrieveTimeEntries(null, null, clientId, false, billableOnly)
                    if (t.isEmpty()) {
                        null
                    } else {
                        val sortedList = t.sortedBy { it.starting }.toList()
                        val body = generateExcel(c, sortedList)

                        ExcelSheet(name = c.clientId, date = sortedList.first().date, excel = body)
                    }
                }
    }

    fun info(clientId: String?, from: LocalDate?, to: LocalDate?): List<ClientInfo> {
        return userService.reactiveCurrentUserDto()
                ?.let { user ->
                    val canSeeMoney = user.admin || user.canViewMoney
                    val email = if (user.admin) null else user.email

                    var sql = "select * from client_overview "
                    var where = false
                    if (clientId != null) {
                        sql += "where client_id = '$clientId'"
                        where = true
                    }
                    if (email != null) {
                        sql += if (where) " and " else " where "
                        sql += "(client_id in (select c1.id from client c1 join client_user cu on cu.client_id = c1.id where cu.email = '$email'))"
                    }

                    TODO("refactor this")
//                    jdbi.withHandle<List<ClientInfo>, Exception> { handle ->
//                        val clientInfos = handle.createQuery(sql)
//                                .map { row ->
//                                    ClientInfo(
//                                            id = row.getColumn("client_id", String::class.java) ?: "<<ID>>",
//                                            name = row.getColumn("client_name", String::class.java)
//                                                    ?: "<<<< NAME >>>>>",
//                                            maxMinutes = row.getColumn("client_max_minutes", Integer::class.java)?.toInt(),
//                                            rateInCentsPerHour = row.getColumn("client_rate_in_cents_per_hour", Integer::class.java)?.toInt(),
//                                            billedAmount = null,
//                                            billedMinutes = 0,
//                                            openAmount = null,
//                                            openMinutes = 0,
//                                            remainingAmount = null,
//                                            remainingMinutes = null,
//                                            projects = listOf(
//                                                    ProjectInfo(
//                                                            projectId = row.getColumn("project_id", java.lang.Long::class.java)?.toLong(),
//                                                            name = row.getColumn("project_name", String::class.java)
//                                                                    ?: "<<< NAME >>>>",
//                                                            maxMinutes = row.getColumn("project_max_minutes", Integer::class.java)?.toInt(),
//                                                            billable = row.getColumn("project_billable", java.lang.Boolean::class.java)?.booleanValue()
//                                                                    ?: false,
//                                                            rateInCentsPerHour = row.getColumn("project_rate_in_cents_per_hour", Integer::class.java)?.toInt(),
//                                                            billedMinutes = row.getColumn("project_billed_minutes", Integer::class.java)?.toInt()
//                                                                    ?: 0,
//                                                            billedAmount = null,
//                                                            openMinutes = row.getColumn("project_open_minutes", Integer::class.java)?.toInt()
//                                                                    ?: 0,
//                                                            openAmount = null,
//                                                            remainingMinutes = null,
//                                                            remainingAmount = null
//                                                    )
//                                            )
//                                    )
//                                }
//                                .list()
//                                .toList()
//
//                        clientInfos
//                                .groupBy { it.id }
//                                .map { e ->
//                                    val client = e.value.first()
//                                    val projects = e.value.flatMap { it.projects }
//
//                                    val projectWithRemainingMinutes =
//                                            projects.map { project ->
//                                                project.copy(
//                                                        remainingMinutes = project.maxMinutes?.let { max(it - project.billedMinutes - project.openMinutes, 0) }
//                                                )
//                                            }
//
//                                    val cProjects =
//                                            if (canSeeMoney) {
//                                                projectWithRemainingMinutes
//                                                        .map { project ->
//                                                            val rateN = if (project.billable) project.rateInCentsPerHour?.let { if (it == 0) null else it }
//                                                                    ?: client.rateInCentsPerHour else null
//                                                            project.copy(
//                                                                    rateInCentsPerHour = rateN,
//                                                                    billedAmount = rateN?.let { rate -> project.billedMinutes * rate / 60.0 },
//                                                                    openAmount = rateN?.let { rate -> project.openMinutes * rate / 60.0 },
//                                                                    remainingAmount = rateN?.let { rate -> project.remainingMinutes?.let { it * rate / 60.0 } }
//                                                            )
//                                                        }
//                                            } else
//                                                projectWithRemainingMinutes
//
//
//                                    val clientBase = client.copy(
//                                            rateInCentsPerHour = if (canSeeMoney) client.rateInCentsPerHour else null,
//                                            projects = cProjects.sortedBy { it.name }
//                                    )
//
//                                    val clientWithMinutes = clientBase.copy(
//                                            billedMinutes = clientBase.projects.filter { it.billable }.sumBy { it.billedMinutes },
//                                            openMinutes = clientBase.projects.filter { it.billable }.sumBy { it.openMinutes }
//                                    )
//
//                                    val remainingMinutes = clientWithMinutes.maxMinutes?.let { mm -> max(0, mm - clientWithMinutes.billedMinutes - clientWithMinutes.openMinutes) }
//
//                                    val clientWithProjects = clientWithMinutes.copy(
//                                            remainingMinutes = remainingMinutes,
//
//                                            billedAmount = clientWithMinutes.rateInCentsPerHour?.let { rate -> rate * clientWithMinutes.billedMinutes / 60.0 },
//                                            openAmount = clientBase.rateInCentsPerHour?.let { rate -> rate * clientWithMinutes.openMinutes / 60.0 },
//                                            remainingAmount = clientBase.rateInCentsPerHour?.let { rate -> remainingMinutes?.let { rate * it / 60.0 } }
//                                    )
//
//                                    if (clientId == null) {
//                                        clientWithProjects.copy(projects = listOf())
//                                    } else {
//                                        clientWithProjects
//                                    }
//                                }
//                    }
                } ?: listOf()
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
        val rateInCentsPerHour: Int? = null,
        val maxMinutes: Int? = null,
        val billedMinutes: Int,
        val billedAmount: Double? = null,
        val openMinutes: Int,
        val openAmount: Double? = null,
        val remainingMinutes: Int? = null,
        val remainingAmount: Double? = null
)
