import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.22.0"
  val playFrontendVersion = "7.23.0-play-28"
  val mongoVersion = "1.3.0"
  val cryptoJsonVersion = "7.3.0"
  val jodaVersion = "2.9.4"
  val cachingClientVersion = "10.0.0-play-28"

  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.16"
  val mockitoScalaVersion = "1.17.14"
  val flexMarkVersion = "0.64.8"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"   % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"           % playFrontendVersion,
    "com.typesafe.play" %% "play-joda-forms"              % PlayVersion.current,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"           % mongoVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-28"          % cryptoJsonVersion,
    "com.typesafe.play" %% "play-json-joda"               % jodaVersion,
  )

  private val allTestsScope = "test,it"

  private val commonTests = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28" % bootstrapVersion         % allTestsScope,
    "com.typesafe.play"       %% "play-test"              % PlayVersion.current      % allTestsScope,
    "org.scalatest"           %% "scalatest"              % scalatestVersion         % allTestsScope,
    "org.scalatestplus.play"  %% "scalatestplus-play"     % scalatestPlusPlayVersion % allTestsScope,
    "com.vladsch.flexmark"    % "flexmark-all"            % flexMarkVersion          % allTestsScope // for scalatest 3.2.x
  )

  private val testOnly = Seq(
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % mongoVersion        % Test,
    "org.scalatestplus"      %% "scalacheck-1-17"         % "3.2.16.0"          % Test,
    "org.scalatestplus"      %% "mockito-4-11"            % "3.2.16.0"          % Test,
    "org.mockito"            %% "mockito-scala-scalatest" % mockitoScalaVersion % Test,
    "org.jsoup"              %  "jsoup"                   % "1.16.1"            % Test
  )

  private val integrationTestOnly = Seq()

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly ++ integrationTestOnly

}
