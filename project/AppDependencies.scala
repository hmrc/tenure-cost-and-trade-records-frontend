import play.core.PlayVersion
import sbt.*

object AppDependencies {

  val bootstrapVersion              = "9.11.0"
  val playFrontendVersion           = "12.0.0"
  val playConditionalMappingVersion = "3.3.0"
  val mongoVersion                  = "2.6.0"
  val cryptoJsonVersion             = "8.2.0"

  // Test dependencies
  val scalatestPlusPlayVersion       = "7.0.1"
  val scalatestVersion               = "3.2.19"
  val scalaTestPlusScalaCheckVersion = "3.2.19.0"
  val scalaTestPlusMockitoVersion    = "3.2.19.0"
  val flexMarkVersion                = "0.64.8"
  val jsoupVersion                   = "1.20.1"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % playFrontendVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % playConditionalMappingVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % mongoVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"                   % cryptoJsonVersion
  )

  private val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapVersion               % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % mongoVersion                   % Test,
    "org.playframework"      %% "play-test"               % PlayVersion.current            % Test,
    "org.scalatest"          %% "scalatest"               % scalatestVersion               % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"      % scalatestPlusPlayVersion       % Test,
    "org.scalatestplus"      %% "scalacheck-1-18"         % scalaTestPlusScalaCheckVersion % Test,
    "org.scalatestplus"      %% "mockito-5-12"            % scalaTestPlusMockitoVersion    % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % flexMarkVersion                % Test, // for scalatest 3.2.x
    "org.jsoup"               % "jsoup"                   % jsoupVersion                   % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}
