/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import connectors.Audit
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.*
import models.Session
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.{UltimatelyResponsibleBusinessInsurancePageId, *}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6048NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing().when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6048 = Session(
    "99996048004",
    FOR6048,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6045TextArea),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree6045TextArea)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "return a function that goes to how rent is currently fixed  page when UR building insurance is completed" in {
      val answers = session6048.copy(
        aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo)
      )
      navigator
        .nextPage(HowIsCurrentRentFixedId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

    "return a function that goes to how rent is currently fixed  page when UR building insurance is completed123" in {
      val answers = session6048.copy(
        aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo)
      )
      navigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to work carried out condition page when property updates answer is no123" in {

      val session = session6048.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6048.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumOrPremium = Some(AnswerYes))
          )
        )
      )
      navigator
        .nextPage(PayCapitalSumId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumAmountDetailsController
        .show()
    }

    "return a function that goes to does rent include fixture and fittings  when is parking rent paid separately has been completed123" in {
      navigator
        .nextPage(PayCapitalSumDetailsId, session6048)
        .apply(session6048) shouldBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
        .show()
    }

  }
}
