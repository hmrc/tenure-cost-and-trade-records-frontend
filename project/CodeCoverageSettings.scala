import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  private val excludedPackages: Seq[String] = Seq(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    ".*Routes.*",
    ".*RoutesPrefix.*",
    ".*Filters?",
    "MicroserviceAuditConnector",
    "Module",
    "config.InternalAuthTokenInitialiserImpl",
    "GraphiteStartUp",
    """.*\.Reverse[^.]*;""",
    """views\.*""",
    ".*.template.scala",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*"
  )

  val settings: Seq[Setting[_]] = Seq(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 76.9,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )

}
