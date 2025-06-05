import org.irundaia.sass.Minified
import uk.gov.hmrc.DefaultBuildSettings.itSettings

val defaultPort = 9526
val appName     = "tenure-cost-and-trade-records-frontend"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.7.1"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    scalacOptions += "-Wconf:src=routes/:s",
    scalacOptions += "-Wconf:msg=Flag .* set repeatedly:s",
    scalacOptions += "-Wconf:msg=unused import&src=views/&origin=^(views\\.html\\.\\W|controllers|models\\.\\W|play|_root_):s",
    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a \\`using\\` clause&src=views/:s",
    scalacOptions += "-Wconf:cat=deprecation&msg=looks like useless nesting:s",
    scalacOptions += "-Wconf:cat=deprecation&src=/lettingHistory/:s",
    scalacOptions += "-feature",
    javaOptions += "-XX:+EnableDynamicAgentLoading",
    libraryDependencies ++= AppDependencies.appDependencies,
    PlayKeys.playDefaultPort := defaultPort
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
