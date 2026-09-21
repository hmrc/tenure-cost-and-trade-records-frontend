/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.aboutyouandtheproperty

import models.ForType.*
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.common.AnswersYesNo.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class CheckYourAnswersAboutThePropertyControllerSpec extends ControllerSpec:

  def checkYourAnswersAboutThePropertyController6010Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6010No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6010None(): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6015Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6015No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6015None(): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6015, aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6030Yes(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6030No(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6030None(): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6030, aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6020(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6020, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyController6076(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6076, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyControllerYes6045(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6045, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyControllerNo6045(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyNo)
  ): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6045, aboutYouAndTheProperty = aboutYouAndTheProperty),
      mockSessionRepository
    )

  def checkYourAnswersAboutThePropertyControllerNone(): CheckYourAnswersAboutThePropertyController =
    CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(forType = FOR6010, aboutYouAndTheProperty = None),
      mockSessionRepository
    )

  "GET /" should {
    "return 200 6010 about you and the property CYA with tied goods yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010Yes().show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutThePropertyController6010Yes().show(getRequest)
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show().url
      )
    }

    "return 200 6010 about you and the property CYA with tied goods no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010No().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
      )
    }

    "return 200 6010 about you and the property CYA with no tied goods in the session" in {
      val result = checkYourAnswersAboutThePropertyController6010None().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
      )
    }

    "return 200 6015 about you and the property CYA with premises license granted yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015Yes().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show().url
      )
    }

    "return 200 6015 about you and the property CYA with premises license granted no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015No().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
      )
    }

    "return 200 6015 about you and the property CYA with no premises license granted in the session" in {
      val result = checkYourAnswersAboutThePropertyController6015None().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
      )
    }

    "return 200 6030 about you and the property CYA with charity question yes in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030Yes().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.TradingActivityController.show().url
      )
    }

    "return 200 6030 about you and the property CYA with charity question no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030No().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CharityQuestionController.show().url
      )
    }

    "return 200 6030 about you and the property CYA with no charity question in the session" in {
      val result = checkYourAnswersAboutThePropertyController6030None().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CharityQuestionController.show().url
      )
    }

    "return 200 6020 about you and the property CYA with no in the session" in {
      val result = checkYourAnswersAboutThePropertyController6020().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session 6076" in {
      val result = checkYourAnswersAboutThePropertyController6076().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session 6045" in {
      val result = checkYourAnswersAboutThePropertyControllerYes6045().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
      )
    }

    "return 200 no about you and the property CYA in the session" in {
      val result = checkYourAnswersAboutThePropertyControllerNone().show(getRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }

    def controller(
      partTwo: AboutYouAndThePropertyPartTwo,
      isPossibleWelsh: Boolean
    ) = CheckYourAnswersAboutThePropertyController(
      stubMessagesControllerComponents(),
      aboutYouAndThePropertyNavigator,
      checkYourAnswersAboutThePropertyView,
      preEnrichedActionRefiner(
        forType = FOR6048,
        aboutYouAndThePropertyPartTwo = Option(partTwo),
        isWelsh = isPossibleWelsh
      ),
      mockSessionRepository
    )

    "return correct backLinks for FOR6048 when some occupiers - family members in the property" in {
      val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(
        partsUnavailable = Option(AnswerYes)
      )
      val result  = controller(partTwo, isPossibleWelsh = false).show(getRequest)

      status(result)        shouldBe OK
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0).url
      )
    }

    "return correct backLinks when no occupiers  - family members in the property" in {
      val partTwo = prefilledAboutYouAndThePropertyPartTwo6048.copy(
        partsUnavailable = Option(AnswerNo)
      )
      val result  = controller(partTwo, isPossibleWelsh = false).show(getRequest)

      status(result)        shouldBe OK
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutThePropertyController6010Yes().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
