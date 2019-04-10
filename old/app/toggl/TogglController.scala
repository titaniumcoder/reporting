package toggl

import java.time.LocalDate

import frontend.AuthenticatedUserAction
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class TogglController @Inject()(cc: ControllerComponents, WithBasicAuth: AuthenticatedUserAction, c: TogglClient)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  def clients() = WithBasicAuth.async {
    import toggl.TogglModel.Client._

    c.clients().map(c => Ok(Json.toJson(Map("clients" -> c))))
  }

  def cash(from: Option[LocalDate], to: Option[LocalDate]) = WithBasicAuth.async {
    val startOfYear = LocalDate.now().withDayOfYear(1)
    val endOfYear = LocalDate.now().plusYears(1).withDayOfYear(1).minusDays(1)

    c.cash(from.getOrElse(startOfYear), to.getOrElse(endOfYear))
      .map(x => Ok(Json.toJson(Map("cash" -> x))))
  }
}
