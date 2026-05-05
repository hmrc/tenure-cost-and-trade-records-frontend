import sbt.*

object AppDependencies {

  private val bootstrapVersion              = "10.7.0"
  private val playFrontendVersion           = "13.4.0"
  private val voServiceVersion              = "0.11.0"
  private val playConditionalMappingVersion = "3.5.0"
  private val mongoVersion                  = "2.12.0"
  private val cryptoJsonVersion             = "8.4.0"

  // Test dependencies
  private val voTestVersion = "0.5.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc-play-30"            % playFrontendVersion,
    "uk.gov.hmrc"       %% "vo-frontend-service"                   % voServiceVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping-play-30" % playConditionalMappingVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"                    % mongoVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"                   % cryptoJsonVersion
  )

  private val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "uk.gov.hmrc" %% "vo-unit-test"           % voTestVersion    % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

  val itDependencies: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "vo-integration-test" % voTestVersion % Test
  )

}
