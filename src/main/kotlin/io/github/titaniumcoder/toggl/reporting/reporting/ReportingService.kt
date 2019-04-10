package io.github.titaniumcoder.toggl.reporting.reporting

import io.github.titaniumcoder.toggl.reporting.transformers.ViewModel
import org.springframework.stereotype.Service

@Service
class ReportingService {
    fun generateExcel(model: ViewModel.ReportingModel): ByteArray = TODO()

    /*
package excel

import java.awt.Color
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Date

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.{XSSFCellStyle, XSSFColor, XSSFFont, XSSFWorkbook}

import scala.concurrent.{ExecutionContext, Future}
import Alignment._
import javax.inject.Inject

class TimesheetExcelService @Inject()(implicit ec: ExecutionContext) extends TimesheetService {

  import TimesheetModel._
  import reporting.ReportingModel._

  override def transformViewModelToTimesheetModel(model: ViewModel) = Future {
    val cells: Map[(Int, Int), Cell] =
      headerCells(model) ++ timeCells(model) ++ projectCells(model)

    Timesheet(model.client, cells)
  }

  private def headerCells(model: ViewModel): Map[(Int, Int), Cell] = {
    if (model.timeEntries.isEmpty) Map() else {
      val headerStyle = Style(font = Font(fontSize = 14, bold = true))
      val bb = Border(width = 1)
      val tableHeaderStyle = Style(font = Font(bold = true), borders = Borders(top = bb, left = bb, right = bb, bottom = bb))

      Map(
        (0, 0) -> Cell(
          content = model.timeEntries.flatMap(_.map(_.startdate)).headOption,
          mergeCols = Some(5),
          style = headerStyle.copy(format = Some("\"Arbeitszeit \"MMMM yyyy"))),
        (0, 5) -> Cell(content = "Rico Metzger", mergeCols = Some(2), style = headerStyle.copy(alignment = Right)),

        (2, 0) -> Cell(content = "Datum", style = tableHeaderStyle),
        (2, 1) -> Cell(content = "Summe Tag", style = tableHeaderStyle.copy(alignment = Right)),
        (2, 2) -> Cell(content = "Von", style = tableHeaderStyle.copy(alignment = Right)),
        (2, 3) -> Cell(content = "Bis", style = tableHeaderStyle.copy(alignment = Center)),
        (2, 4) -> Cell(content = "Zeit", style = tableHeaderStyle.copy(alignment = Right)),
        (2, 5) -> Cell(content = "Projekt", style = tableHeaderStyle),
        (2, 6) -> Cell(content = "Bemerkung", style = tableHeaderStyle)
      )
    }
  }

  private def timeCells(model: ViewModel): Map[(Int, Int), Cell] = model.timeEntries match {
    case Nil => Map()
    case _ =>
      val bb = Border(width = 1)
      val gb = Border(width = 1, color = Color.LIGHT_GRAY)

      val bordered = Borders(left = bb, right = bb, top = bb, bottom = bb)

      def oneDay(row: Int, dayEntries: List[TimeEntry]) = {
        val sum = dayEntries.map(_.minutes).sum

        dayEntries.zipWithIndex.flatMap {
          case (entry, idx) =>
            val firstRowInDay = idx == 0
            val lastRowInDay = idx == dayEntries.size - 1
            val cb = Style(borders = bordered.copy(top = if (firstRowInDay) bb else gb, bottom = if (lastRowInDay) bb else gb))

            (if (firstRowInDay) {
              (row, 0) -> Cell(content = entry.day, style = cb.copy(format = Some("dd.MM.yyyy"), borders = cb.borders.copy(bottom = bb)), mergeRows = Some(dayEntries.size)) ::
                (row, 1) -> Cell(content = formatTime(sum), style = cb.copy(format = Some("[h]:mm"), alignment = Right, borders = cb.borders.copy(bottom = bb)), mergeRows = Some(dayEntries.size)) :: Nil
            } else Nil) ++ (
              (row + idx, 2) -> Cell(content = entry.startdate, style = cb.copy(alignment = Center, format = Some("hh:mm"))) ::
                (row + idx, 3) -> Cell(content = entry.enddate, style = cb.copy(alignment = Center, format = Some("hh:mm"))) ::
                (row + idx, 4) -> Cell(content = formatTime(entry.minutes), style = cb.copy(format = Some("[h]:mm"), alignment = Right)) ::
                (row + idx, 5) -> Cell(content = entry.project, style = cb) ::
                (row + idx, 6) -> Cell(content = entry.description, style = cb) ::
                Nil
              )
        }
      }

      model
        .timeEntries
        .foldLeft(List[(Int, List[TimeEntry])]())((a, b) => {
          val nRow = a.headOption.map(x => x._1 + x._2.size).getOrElse(3)
          (nRow, b) :: a
        })
        .flatMap(x => oneDay(x._1, x._2)).toMap
  }


  private def projectCells(model: ViewModel): Map[(Int, Int), Cell] = model.timeEntries match {
    case Nil => Map()
    case _ =>
      val bb = Border(width = 1)
      val nb = Border()

      val firstRow = model.timeEntries.flatten.size + 4

      val total = model.timeEntries.flatMap(_.map(_.minutes)).sum
      val totalPerProject = model.timeEntries.flatten.groupBy(e => e.project).map {
        case (project, timeentry) => project.getOrElse("<<< Kein Projekt >>>") -> timeentry.map(_.minutes).sum
      }.toList

      def createStyle(first: Boolean, last: Boolean, left: Boolean, right: Boolean) = {
        Style(
          font = Font(bold = true), borders = Borders(left = if (left) bb else nb, right = if (right) bb else nb, top = if (first) bb else nb, bottom = if (last) bb else nb)
        )
      }

      def createRow(row: Int, name: String, seconds: Int, first: Boolean, last: Boolean) = {
        List(
          (row, 0) -> Cell(content = name, mergeCols = Some(3), style = createStyle(first = first, last = last, left = true, right = true)),
          (row, 3) -> Cell(content = formatTime(seconds), style = createStyle(first = first, last = last, left = true, right = false).copy(format = Some("[h]:mm"), alignment = Right)),
          (row, 4) -> Cell(content = formatNumeric(seconds), style = createStyle(first = first, last = last, left = false, right = true).copy(format = Some("0.00"), alignment = Right))
        )
      }

      (List(
        (firstRow, 0) -> Cell(content = "Summen:", mergeCols = Some(3), style = Style(font = Font(bold = true))),
        (firstRow, 3) -> Cell(content = "(Stunden):", style = Style(font = Font(bold = true), alignment = Right)),
        (firstRow, 4) -> Cell(content = "(dezimal):", style = Style(font = Font(bold = true), alignment = Right)),

        (firstRow + 1, 0) -> Cell(content = "Total:", mergeCols = Some(3), style = Style(font = Font(bold = true))),
        (firstRow + 1, 3) -> Cell(content = formatTime(total), style = Style(font = Font(bold = true), format = Some("[h]:mm"), alignment = Right)),
        (firstRow + 1, 4) -> Cell(content = formatNumeric(total), style = Style(font = Font(bold = true), format = Some("0.00"), alignment = Right)),

        (firstRow + 3, 0) -> Cell(content = "Pro Projekt:", mergeCols = Some(3), style = createStyle(first = true, last = false, left = true, right = true)),
        (firstRow + 3, 3) -> Cell(content = None, mergeCols = Some(2), style = createStyle(first = true, last = false, left = true, right = true))
      ) ++ totalPerProject.sortBy(_._1).zipWithIndex.flatMap {
        case ((project, sum), row) => createRow(firstRow + row + 4, project, sum, first = false, last = row == totalPerProject.size - 1)
      }).toMap
  }

  // this method is unfortunately very stateful but this is mainly due to the OOXML being stateful itself...
  override def generateExcel(sm: Timesheet) = Future {
    val workbook = new XSSFWorkbook()

    val styles: Map[Style, XSSFCellStyle] = generateCellStyles(workbook, sm)

    def toDate(t: LocalDateTime): Date = Date.from(t.atZone(ZoneId.systemDefault()).toInstant)

    val sheet = workbook.createSheet(sm.name)

    def getCell(r: Int, c: Int) =
      Option(sheet.getRow(r)).getOrElse(sheet.createRow(r))
        .getCell(c, CREATE_NULL_AS_BLANK)

    sm.cells.foreach {
      case ((row, col), content) =>
        val cellM = getCell(row, col)
        content.content match {
          case x: String => cellM.setCellValue(x)
          case Some(x: String) => cellM.setCellValue(x)
          case x: Double => cellM.setCellValue(x)
          case x: LocalDateTime => cellM.setCellValue(toDate(x)) // TODO systemdefault could be wrong, need to check it
          case x: LocalDate => cellM.setCellValue(toDate(x.atStartOfDay())) // TODO systemdefault could be wrong, need to check it
          case Some(x: LocalDateTime) => cellM.setCellValue(toDate(x)) // TODO systemdefault could be wrong, need to check it
          case None => cellM.setCellValue("") // TODO may be wrong, but for now it's okay
          case x => throw new UnsupportedOperationException("Unknown value type: " + x + "[" + x.getClass + "]") // TODO bad change it or don't support it...
        }

        cellM.setCellStyle(styles.getOrElse(content.style, addCellStyle(workbook, content.style)))

        (content.mergeRows.getOrElse(1), content.mergeCols.getOrElse(1)) match {
          case (1, 1) => // do nothing
          case (r, c) =>
            sheet.addMergedRegion(new CellRangeAddress(row, row + r - 1, col, col + c - 1))
            val merged = for {
              rs <- row until row + r
              cs <- col until col + c
              if rs != row || cs != col
            } yield (rs, cs)

            merged.foreach {
              case (y, x) =>
                val cc = getCell(y, x)
                cc.setCellStyle(cellM.getCellStyle)
            }
        }
    }

    sheet.getColumnHelper.setColWidth(0, 15.0)
    sheet.getColumnHelper.setColWidth(1, 15.0)
    sheet.getColumnHelper.setColWidth(2, 10.5)
    sheet.getColumnHelper.setColWidth(3, 10.5)
    sheet.getColumnHelper.setColWidth(4, 9.0)
    sheet.getColumnHelper.setColWidth(5, 42)
    sheet.getColumnHelper.setColWidth(6, 25)

    // printing setup
    sheet.setAutobreaks(true)

    sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.TopMargin, 0.78)
    sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.BottomMargin, 0.78)
    sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.LeftMargin, 0.25)
    sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.RightMargin, 0.25)

    val footer = sheet.getFooter
    footer.setLeft(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
    footer.setRight(s"Seite ${org.apache.poi.hssf.usermodel.HeaderFooter.page()} von ${org.apache.poi.hssf.usermodel.HeaderFooter.numPages()}")

    val ps = sheet.getPrintSetup
    ps.setPaperSize(PaperSize.A4_PAPER)
    ps.setFitWidth(1)
    ps.setFitHeight(100)
    ps.setOrientation(PrintOrientation.PORTRAIT)

    // TODO this should be moved to the model too but for now it's fine for the demo effect...
    sheet.autoSizeColumn(5)
    sheet.autoSizeColumn(6)

    sheet.setRepeatingRows(CellRangeAddress.valueOf("1:3"))

    sheet.setFitToPage(true)

    val bos = new ByteArrayOutputStream()
    workbook.write(bos)
    bos.toByteArray
  }

  private def generateCellStyles(workbook: XSSFWorkbook, sheet: Timesheet): Map[Style, XSSFCellStyle] = {
    // generate all fonts in the workbook
    val createHelper = workbook.getCreationHelper

    def toExcelAlignment(alignment: Alignment.HorizontalAlignment) = alignment match {
      case Left => HorizontalAlignment.LEFT
      case Center => HorizontalAlignment.CENTER
      case Right => HorizontalAlignment.RIGHT
    }

    val fonts =
      sheet.cells.values.map(_.style.font)
        .toList
        .distinct
        .map(f => {
          val f1 = workbook.createFont()
          f1.setFontHeightInPoints(f.fontSize.toShort)
          f1.setBold(f.bold)
          f1.setItalic(f.italic)
          f -> f1
        })
        .toMap

    // create all styles
    sheet.cells.values.map(_.style)
      .toList
      .distinct
      .map(cs => {
        val s = workbook.createCellStyle()
        s.setAlignment(toExcelAlignment(cs.alignment))
        s.setFont(fonts(cs.font))

        cs.format.foreach(format =>
          s.setDataFormat(createHelper.createDataFormat().getFormat(format))
        )

        s.setBorderTop(borderStyle(cs.borders.top.width))
        s.setBorderBottom(borderStyle(cs.borders.bottom.width))
        s.setBorderLeft(borderStyle(cs.borders.left.width))
        s.setBorderRight(borderStyle(cs.borders.right.width))

        s.setTopBorderColor(new XSSFColor(cs.borders.top.color))
        s.setBottomBorderColor(new XSSFColor(cs.borders.bottom.color))
        s.setLeftBorderColor(new XSSFColor(cs.borders.left.color))
        s.setRightBorderColor(new XSSFColor(cs.borders.right.color))

        s.setVerticalAlignment(VerticalAlignment.TOP)

        cs -> s
      })
      .toMap
  }


  private def addCellStyle(workbook: XSSFWorkbook, style: Style): XSSFCellStyle = {
    val cs = workbook.createCellStyle()
    val f = Option(workbook.findFont(style.font.bold, XSSFFont.DEFAULT_FONT_COLOR, style.font.fontSize.toShort, XSSFFont.DEFAULT_FONT_NAME, style.font.italic, false, 0, 0))
    cs.setFont(
      f match {
        case None =>
          val f1 = workbook.createFont()
          f1.setBold(style.font.bold)
          f1.setItalic(style.font.italic)
          f1.setFontHeight(style.font.fontSize.toShort)
          f1
        // create the font
        case Some(x) => x
      }
    )
    cs
  }

  private def borderStyle(width: Int): BorderStyle = width match {
    case 0 => BorderStyle.NONE
    case 1 => BorderStyle.THIN
    case _ => BorderStyle.THICK
  }

  private def formatTime(minutes: Int): Double = minutes.toDouble / 1440

  private def formatNumeric(minutes: Int): Double = minutes.toDouble / 60
}
     */
}