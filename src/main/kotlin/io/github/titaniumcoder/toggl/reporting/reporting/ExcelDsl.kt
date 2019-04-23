package io.github.titaniumcoder.toggl.reporting.reporting

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Excel {
    private val sheets = arrayListOf<Sheet>()

    fun sheet(name: String, init: Sheet.() -> Unit): Sheet {
        val sheet = Sheet(name)
        sheet.init()
        sheets.add(sheet)
        return sheet
    }

    fun render(): ByteArray {
        val workbook = XSSFWorkbook()

        val styles: Map<Style, XSSFCellStyle> = calculateCellstyles(workbook, sheets)

        for (s in sheets) {
            s.render(workbook, styles)
        }

        ByteArrayOutputStream().use {
            workbook.write(it)
            workbook.close()
            return it.toByteArray()
        }
    }


    private fun calculateCellstyles(workbook: XSSFWorkbook, sheets: List<Sheet>): Map<Style, XSSFCellStyle> {
        val defaultFontSize = 12

        data class InternalFont(val size: Int, val bold: Boolean)

        fun styleToFont(style: Style) = InternalFont(style.fontSize ?: defaultFontSize, style.bold)

        val createHelper = workbook.creationHelper
        val allStyles = sheets.flatMap { it.styles() }.distinct()
        val fonts = allStyles
                .map { styleToFont(it) }
                .distinct()
                .map {
                    val f1 = workbook.createFont()
                    f1.fontHeightInPoints = it.size.toShort()
                    f1.bold = it.bold
                    it to f1
                }
                .toMap()
        return allStyles
                .map {
                    val s = workbook.createCellStyle()
                    it.alignment?.let { a ->
                        s.alignment = when (a) {
                            HorizontalAlignment.Left -> org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT
                            HorizontalAlignment.Center -> org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER
                            HorizontalAlignment.Right -> org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT
                        }
                    }
                    s.setFont(fonts.getValue(styleToFont(it)))
                    it.format?.let { f ->
                        s.dataFormat = createHelper.createDataFormat().getFormat(f)
                    }

                    it.top?.let { b ->
                        s.borderTop = BorderStyle.THIN
                        s.topBorderColor = if (b == 1) IndexedColors.BLACK.index else IndexedColors.GREY_25_PERCENT.index
                    }
                    it.bottom?.let { b ->
                        s.borderBottom = BorderStyle.THIN
                        s.bottomBorderColor = if (b == 1) IndexedColors.BLACK.index else IndexedColors.GREY_25_PERCENT.index
                    }
                    it.left?.let { b ->
                        s.borderLeft = BorderStyle.THIN
                        s.leftBorderColor = if (b == 1) IndexedColors.BLACK.index else IndexedColors.GREY_25_PERCENT.index
                    }
                    it.right?.let { b ->
                        s.borderRight = BorderStyle.THIN
                        s.rightBorderColor = if (b == 1) IndexedColors.BLACK.index else IndexedColors.GREY_25_PERCENT.index
                    }

                    s.verticalAlignment = VerticalAlignment.TOP

                    it to s
                }
                .toMap()
    }
}

class Sheet(val name: String) {
    private val cells = arrayListOf<Cell>()

    fun cell(row: Int, col: Int, init: Cell.() -> Unit): Cell {
        val cell = Cell(row, col)
        cell.init()
        cells.add(cell)
        return cell
    }

    internal fun styles() =
            cells
                    .map { it.style }
                    .distinct()

    internal fun render(workbook: XSSFWorkbook, styles: Map<Style, XSSFCellStyle>) {
        val sheet = workbook.createSheet(name)

        cells.forEach { it.render(sheet, styles) }

        sheet.columnHelper.setColWidth(0, 15.0)
        sheet.columnHelper.setColWidth(1, 15.0)
        sheet.columnHelper.setColWidth(2, 10.5)
        sheet.columnHelper.setColWidth(3, 10.5)
        sheet.columnHelper.setColWidth(4, 9.0)
        sheet.columnHelper.setColWidth(5, 42.0)
        sheet.columnHelper.setColWidth(6, 25.0)

        // printing setup
        sheet.autobreaks = true

        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.TopMargin, 0.78)
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.BottomMargin, 0.78)
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.LeftMargin, 0.25)
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.RightMargin, 0.25)

        val footer = sheet.footer
        footer.left = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        footer.right = "Seite ${org.apache.poi.hssf.usermodel.HeaderFooter.page()} von ${org.apache.poi.hssf.usermodel.HeaderFooter.numPages()}"

        val ps = sheet.printSetup
        ps.setPaperSize(PaperSize.A4_PAPER)
        ps.fitWidth = 1
        ps.fitHeight = 100
        ps.orientation = PrintOrientation.PORTRAIT

        // TODO this should be moved to the model too but for now it's fine for the demo effect...
        sheet.autoSizeColumn(5)
        sheet.autoSizeColumn(6)

        sheet.repeatingRows = CellRangeAddress.valueOf("1:3")

        sheet.fitToPage = true
    }
}

fun toDate(t: LocalDateTime): Date = Date.from(t.atZone(ZoneId.systemDefault()).toInstant())
fun toDate(t: LocalDate): Date = Date.from(t.atStartOfDay(ZoneId.systemDefault()).toInstant())

class Cell(private val row: Int, private val col: Int) {
    internal var style: Style = Style()

    var content: Any? = null
    var mergedRows: Int = 1
    var mergedCols: Int = 1

    var format: String?
        get() = style.format
        set(value) {
            style = style.copy(format = value)
        }

    var top: Int?
        get() = style.top
        set(value) {
            style = style.copy(top = value)
        }

    var left: Int?
        get() = style.left
        set(value) {
            style = style.copy(left = value)
        }

    var bottom: Int?
        get() = style.bottom
        set(value) {
            style = style.copy(bottom = value)
        }

    var right: Int?
        get() = style.right
        set(value) {
            style = style.copy(right = value)
        }

    var size: Int?
        get() = style.fontSize
        set(value) {
            style = style.copy(fontSize = value)
        }

    var bold: Boolean
        get() = style.bold
        set(value) {
            style = style.copy(bold = value)
        }

    var alignment: HorizontalAlignment?
        get() = style.alignment
        set(value) {
            style = style.copy(alignment = value)
        }

    private fun getCell(sheet: XSSFSheet, r: Int, c: Int): XSSFCell =
            (sheet.getRow(r) ?: sheet.createRow(r))
                    .getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)

    internal fun render(sheet: XSSFSheet, styles: Map<Style, XSSFCellStyle>) {
        val c = getCell(sheet, row, col)

        when (val currentContent = content) {
            is String -> c.setCellValue(currentContent)
            is Double -> c.setCellValue(currentContent)
            is LocalDateTime -> c.setCellValue(toDate(currentContent))
            is LocalDate -> c.setCellValue(toDate(currentContent))
            null -> c.setCellValue("")
            else -> throw UnsupportedOperationException("unknown value type $currentContent (${currentContent.javaClass}) for cell $row / $col")
        }

        val cellStyle = styles.getValue(style)
        c.cellStyle = cellStyle

        if (mergedRows > 1 || mergedCols > 1) {
            val cra = CellRangeAddress(row, row + mergedRows - 1, col, col + mergedCols - 1)
            sheet.addMergedRegion(cra)

            for (cr in cra.firstRow..cra.lastRow) {
                for (cc in cra.firstColumn..(cra.lastColumn)) {
                    if (cr != row || cc != col) {
                        getCell(sheet, cr, cc).cellStyle = cellStyle
                    }
                }
            }
        }
    }
}

enum class HorizontalAlignment {
    Left, Center, Right
}

internal data class Style(
        val format: String? = null,
        val top: Int? = null,
        val bottom: Int? = null,
        val left: Int? = null,
        val right: Int? = null,
        val fontSize: Int? = null,
        val bold: Boolean = false,
        val alignment: HorizontalAlignment? = null
)

fun excel(init: Excel.() -> Unit): Excel {
    val excel = Excel()
    excel.init()
    return excel
}
