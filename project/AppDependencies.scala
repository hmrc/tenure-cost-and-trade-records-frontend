import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion     = "8.4.0"
  val playFrontendVersion  = "8.5.0"
  val mongoVersion         = "1.7.0"
  val cryptoJsonVersion    = "7.6.0"
  val jodaVersion          = "2.9.4"
  val cachingClientVersion = "10.0.0-play-28"

  // Test dependencies
  val scalatestPlusPlayVersion = "7.0.1"
  val scalatestVersion         = "3.2.18"
  val mockitoScalaVersion      = "1.17.30"
  val flexMarkVersion          = "0.64.8"
  val wireMockVersion          = "2.21.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30" % playFrontendVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"         % mongoVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"        % cryptoJsonVersion
  )

  private val allTestsScope = "test,it"

  private val commonTests = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion         % allTestsScope,
    "org.playframework"      %% "play-test"              % PlayVersion.current      % allTestsScope,
    "org.scalatest"          %% "scalatest"              % scalatestVersion         % allTestsScope,
    "org.scalatestplus.play" %% "scalatestplus-play"     % scalatestPlusPlayVersion % allTestsScope,
    "com.vladsch.flexmark"    % "flexmark-all"           % flexMarkVersion          % allTestsScope, // for scalatest 3.2.x
    "com.github.tomakehurst"  % "wiremock"               % wireMockVersion          % allTestsScope
  )

  private val testOnly = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % mongoVersion        % Test,
    "org.scalatestplus" %% "scalacheck-1-17"         % "3.2.17.0"          % Test,
    "org.scalatestplus" %% "mockito-4-11"            % "3.2.17.0"          % Test,
    "org.mockito"       %% "mockito-scala-scalatest" % mockitoScalaVersion % Test,
    "org.jsoup"          % "jsoup"                   % "1.16.1"            % Test
  )

  private val integrationTestOnly = Seq()

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly ++ integrationTestOnly

}
