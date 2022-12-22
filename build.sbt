import uk.gov.hmrc.DefaultBuildSettings.{integrationTestSettings, targetJvm}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "tenure-cost-and-trade-records-frontend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.13.8",
    targetJvm                        := "jvm-11",
    maintainer                       := "voa.service.optimisation@digital.hmrc.gov.uk",
    libraryDependencies              ++= AppDependencies.appDependencies,
    PlayKeys.playDefaultPort         := 9526,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .disablePlugins(JUnitXmlReportPlugin)

addCommandAlias("precommit", ";scalafmt;test:scalafmt;it:scalafmt;coverage;test;it:test;coverageReport")
