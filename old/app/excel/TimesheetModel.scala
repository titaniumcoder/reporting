package excel

import java.awt.Color

import excel.Alignment._

object Alignment {
  sealed trait HorizontalAlignment

  case object Left extends HorizontalAlignment
  case object Right extends HorizontalAlignment
  case object Center extends HorizontalAlignment
}

object TimesheetModel {
  case class Border(width: Int = 0, color: Color = Color.BLACK)
  case class Borders(top: Border = Border(), bottom: Border = Border(), left: Border = Border(), right: Border = Border())
  case class Font(fontSize: Int = 12, bold: Boolean = false, italic: Boolean = false)
  case class Style(font: Font = Font(), borders: Borders = Borders(), alignment: HorizontalAlignment = Left, format: Option[String] = None)

  case class Cell(content: Any, style: Style = Style(), mergeRows: Option[Int] = None, mergeCols: Option[Int] = None)

  case class Timesheet(name: String, cells: Map[(Int, Int), Cell])
}
