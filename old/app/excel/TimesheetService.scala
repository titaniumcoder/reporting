package excel

import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[TimesheetExcelService])
trait TimesheetService {

  import TimesheetModel._
  import reporting.ReportingModel._

  def transformViewModelToTimesheetModel(model: ViewModel): Future[Timesheet]
  def generateExcel(model: Timesheet): Future[Array[Byte]]
}
