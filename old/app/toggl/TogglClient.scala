package toggl

import java.time.LocalDate

import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[TogglWsClient])
trait TogglClient {

  import TogglModel._

  def clients(): Future[List[Client]]
  def cash(from: LocalDate, to: LocalDate): Future[List[Cashout]]
  def entries(clientId: Long, from: LocalDate, to: LocalDate, nonBilledOnly: Boolean, page: Int = 1): Future[TogglReporting]

  def tagBilled(clientId: Long, from: LocalDate, to: LocalDate): Future[Unit]
  def untagBilled(clientId: Long, from: LocalDate, to: LocalDate): Future[Unit]

  def tagBilled(entryId: Long): Future[Unit]
  def untagBilled(entryId: Long): Future[Unit]
}
