package reporting

import java.time.LocalDate

import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[TransformTogglToView])
trait TransformTogglToReporting {

  import ReportingModel._
  import toggl.TogglModel._

  def transformInput(input: TogglReporting, from: LocalDate, to: LocalDate, clientId: Long): Future[ViewModel]
}

