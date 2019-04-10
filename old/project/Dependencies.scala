import play.sbt.PlayImport._
import sbt._

object Dependencies {
  object Versions {
    val poiVersion = "3.17"
    val catsEffectVersion = "1.0.0-RC2"
    val macwireVersion = "2.3.0"
  }

  import Versions._

  lazy val poi = "org.apache.poi" % "poi-ooxml" % poiVersion
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  lazy val macwire = "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided"

  lazy val webjars = Seq(
    "org.webjars.npm" % "bootstrap" % "4.1.3",
    "org.webjars.npm" % "js-tokens" % "3.0.2",
    "org.webjars.npm" % "react" % "16.4.2",
    "org.webjars.npm" % "react-dom" % "16.4.2",
    "org.webjars.npm" % "reactstrap" % "6.0.1",
    "org.webjars.npm" % "moment" % "2.22.2",
    "org.webjars.npm" % "babel-core" % "6.26.0"
  )

}
