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
import models.Session
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent.*
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.ResponsibilityParty.BuildingInsurance.*
import models.submissions.common.ResponsibilityParty.InsideRepairs.*
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6076NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6076Full = Session(
    "99996076004",
    FOR6076,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)
  )

  val session6076NoLeaseback = Session(
    "99996076004",
    FOR6076,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "return a function that goes to current annual rent page when lease or agreement details with yes has been completed" in {
      navigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
        .show()
    }

    "return a function that goes to connected to landlord details when connection to landlord yes has been completed1" in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "return a function that goes to connected to landlord details when connection to landlord yes has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
    }

    "return a function that goes to property use leaseback arrangement when connection to landlord details has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
    }

    "return a function that goes to property use leaseback arrangement when provide details of your lease has been completed" in {
      navigator
        .nextPage(PropertyUseLeasebackAgreementId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }

    "return a function that goes to property use leaseback arrangement no when provide details of your lease has been completed" in {
      navigator
        .nextPage(PropertyUseLeasebackAgreementId, session6076NoLeaseback)
        .apply(
          session6076NoLeaseback
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }

    "return a function that goes to cya when property use leaseback arrangement has been completed" in {
      navigator
        .nextPage(ProvideDetailsOfYourLeasePageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "return a function that goes to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {

      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleOutsideRepairs =
              Some(UltimatelyResponsibleOutsideRepairs(OutsideRepairsLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleOutsideRepairsPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
        .show()
    }

    "return a function that goes to Ultimately responsible IR page when Ultimately Responsible OR has been completed" in {

      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleInsideRepairs =
              Some(UltimatelyResponsibleInsideRepairs(InsideRepairsLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleInsideRepairsPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController
        .show()
    }

    "return a function that goes to rent include trade services page when Ultimately Responsible BI has been completed" in {

      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleBuildingInsurance =
              Some(UltimatelyResponsibleBuildingInsurance(BuildingInsuranceLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController
        .show()
    }

    "return a function that goes to intervals of rent page when method to fix current rent has been completed" in {

      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails = Some(MethodToFixCurrentRentAgreement))
          )
        )
      )
      navigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to ProvideDetailsOfYourLeaseController when forType is FOR6076 and propertyUseLeasebackAgreement is Some('no')" in {
      val sessionWithNoLeaseback = session6076Full.copy(
        aboutLeaseOrAgreementPartOne = Some(
          prefilledAboutLeaseOrAgreementPartOne.copy(
            propertyUseLeasebackAgreement = Some(AnswerNo)
          )
        )
      )

      navigator
        .nextPage(PropertyUseLeasebackAgreementId, sessionWithNoLeaseback)
        .apply(sessionWithNoLeaseback) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }
  }
}
