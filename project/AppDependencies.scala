import sbt.*

object AppDependencies {

  val bootstrapVersion              = "10.7.0"
  val playFrontendVersion           = "12.32.0"
  val playConditionalMappingVersion = "3.5.0"
  val mongoVersion                  = "2.12.0"
  val cryptoJsonVersion             = "8.4.0"

  // Test dependencies
  val scalaTestPlusScalaCheckVersion = "3.2.19.0"
  val scalaTestPlusMockitoVersion    = "3.2.19.0"
  val jsoupVersion                   = "1.22.1"

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
    "org.scalatestplus"      %% "scalacheck-1-18"         % scalaTestPlusScalaCheckVersion % Test,
    "org.scalatestplus"      %% "mockito-5-12"            % scalaTestPlusMockitoVersion    % Test,
    "org.jsoup"               % "jsoup"                   % jsoupVersion                   % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

}
