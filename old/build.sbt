import ReleaseTransformations._

import Dependencies._

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "reporting",
    organization := "io.github.titaniumcoder",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      guice,
      filters,
      ws,
      poi
    ),
    routesImport += "frontend.RoutingFormats._, java.time.LocalDate",
    scalacOptions ++= Seq(
      "-Ypartial-unification",
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      // "-Xlog-implicits"
    ),
    scalacOptions in Test ++= Seq(
      "-Yrangepos"
    ),
    publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository"))),
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies, // : ReleaseStep
      inquireVersions, // : ReleaseStep
      runClean, // : ReleaseStep
      runTest, // : ReleaseStep
      setReleaseVersion, // : ReleaseStep
      commitReleaseVersion, // : ReleaseStep, performs the initial git checks
      tagRelease, // : ReleaseStep
      // publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
      setNextVersion, // : ReleaseStep
      commitNextVersion, // : ReleaseStep
      pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
    )
  )
