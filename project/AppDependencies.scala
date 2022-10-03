import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.7.0"
  val playFrontendVersion = "3.29.0-play-28"

  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.13"
  val mockitoScalaVersion = "1.17.7"
  val flexMarkVersion = "0.64.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % playFrontendVersion,
    "com.typesafe.play"       %% "play-joda-forms"            % PlayVersion.current,
    // temp
    "com.softwaremill.sttp.client3" %% "core" % "3.7.5"
  )

  private val allTestsScope = "test,it"

  private val commonTests = Seq(
    "com.typesafe.play"       %% "play-test"                  % PlayVersion.current      % allTestsScope,
    "org.scalatest"           %% "scalatest"                  % scalatestVersion         % allTestsScope,
    "org.scalatestplus.play"  %% "scalatestplus-play"         % scalatestPlusPlayVersion % allTestsScope,
    "com.vladsch.flexmark"    % "flexmark-all"                % flexMarkVersion          % allTestsScope // for scalatest 3.2.x
  )

  private val testOnly = Seq(
//    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % hmrcMongoVersion         % Test,
    "org.mockito"             %% "mockito-scala-scalatest"    % mockitoScalaVersion      % Test
  )

  private val integrationTestOnly = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion         % IntegrationTest
  )

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly ++ integrationTestOnly

}
