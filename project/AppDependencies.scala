import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootstrapVersion = "7.7.0"
  val playFrontendVersion = "3.32.0-play-28"

  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.13"
  val mockitoScalaVersion = "1.17.7"
  val flexMarkVersion = "0.64.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % playFrontendVersion,
    "com.typesafe.play"       %% "play-joda-forms"            % PlayVersion.current,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28" % "0.73.0",
    "com.typesafe.play" %% "play-json-joda" % "2.9.3",
    "com.typesafe.play" %% "play-joda-forms" % PlayVersion.current,
    "uk.gov.hmrc"          %% "http-caching-client"           % "9.6.0-play-28",

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
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"         % Test,
    "org.scalatest"          %% "scalatest"          % "3.0.8"         % Test,
    "org.scalatestplus"      %% "scalacheck-1-15"    % "3.2.10.0"      % Test,
    "org.pegdown"            %  "pegdown"            % "1.6.0"         % Test,
    "org.mockito"            %  "mockito-core"       % "2.27.0"        % Test,
    "org.scalatestplus"      %% "mockito-3-4"        % "3.2.9.0"       % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % "0.73.0"   % Test,
    "org.mockito"             %% "mockito-scala-scalatest"    % mockitoScalaVersion      % Test
  )

  private val integrationTestOnly = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion         % IntegrationTest
  )

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly ++ integrationTestOnly

}
