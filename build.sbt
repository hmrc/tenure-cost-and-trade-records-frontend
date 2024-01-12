import org.irundaia.sass.Minified
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "tenure-cost-and-trade-records-frontend"

val silencerVersion = "1.7.9"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion                     := 0,
    scalaVersion                     := "2.13.8",
    maintainer                       := "voa.service.optimisation@digital.hmrc.gov.uk",
    libraryDependencies              ++= AppDependencies.appDependencies,
    PlayKeys.playDefaultPort         := 9526,
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())
  .settings(CodeCoverageSettings.settings)
  .settings(
    SassKeys.cssStyle := Minified,
    SassKeys.generateSourceMaps := false,
    Assets / pipelineStages := Seq(digest),
    // Include only final files for assets fingerprinting
    digest / includeFilter := GlobFilter("*.js") || GlobFilter("*.min.css")
  )

addCommandAlias("precommit", ";scalafmt;test:scalafmt;it:scalafmt;coverage;test;it:test;coverageReport")
