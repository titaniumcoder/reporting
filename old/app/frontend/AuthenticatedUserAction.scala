package frontend

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthenticatedUserAction @Inject()(cc: ControllerComponents, config: Configuration, parser: DefaultPlayBodyParsers)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(cc.parsers.anyContent) {

  private lazy val username = config.get[String]("auth.username")
  private lazy val password = config.get[String]("auth.password")

  private def decodeBase64(b: Array[Byte]) =
    new String(java.util.Base64.getDecoder.decode(b))


  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = {
    request.headers.get("Authorization").flatMap { authorization =>
      authorization.split(" ").drop(1).headOption.filter { encoded =>
        val authInfo = new String(decodeBase64(encoded.getBytes)).split(":", 2)
        authInfo.size == 2 && authInfo(0) == username && authInfo(1) == password
      }
    }.map(_ => block(request)).getOrElse {
      Future.successful(Unauthorized("Authentication Failed").withHeaders("WWW-Authenticate" -> "Basic realm=\"titaniumreporting\""))
    }
  }
}