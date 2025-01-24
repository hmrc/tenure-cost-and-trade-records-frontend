import org.irundaia.sass.Minified
import uk.gov.hmrc.DefaultBuildSettings.itSettings

val defaultPort = 9526
val appName     = "tenure-cost-and-trade-records-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.6.3"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    libraryDependencies ++= AppDependencies.appDependencies,
    PlayKeys.playDefaultPort := defaultPort,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s"
  )
  .settings(
    SassKeys.cssStyle := Minified,
    SassKeys.generateSourceMaps := false,
    Assets / pipelineStages := Seq(digest),
    digest / includeFilter := GlobFilter("*.js") || GlobFilter("*.min.css")
  )

lazy val it =
  (project in file("it"))
    .enablePlugins(PlayScala)
    .dependsOn(microservice % "test->test")
    .settings(itSettings())

addCommandAlias("precommit", "scalafmtSbt;scalafmtAll;it/Test/scalafmt;coverage;test;it/test;coverageReport")
