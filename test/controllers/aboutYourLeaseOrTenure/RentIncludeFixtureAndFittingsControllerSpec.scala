/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.aboutYourLeaseOrTenure

import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree}
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentIncludeFixtureAndFittingsControllerSpec extends TestBaseSpec {

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def rentIncludeFixtureAndFittingsController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def rentIncludeFixtureAndFittingsController6020(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(forType = ForTypes.for6020, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def rentIncludeFixtureAndFittingsController6020No(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThreeNo
    )
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(
        forType = ForTypes.for6020,
        aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
      ),
      mockSessionRepo
    )

  def rentIncludeFixtureAndFittingsController6030No(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThreeNo
    )
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(
        forType = ForTypes.for6030,
        aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
      ),
      mockSessionRepo
    )

  def rentIncludeFixtureAndFittingsController6030(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(forType = ForTypes.for6030, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def rentIncludeFixtureAndFittingsNoController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOneNo)
  ) =
    new RentIncludeFixtureAndFittingsController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      rentIncludeFixtureAndFittingsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  "RentIncludeFixtureAndFittings controller" should {
    "return 200" in {
      val result = rentIncludeFixtureAndFittingsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 200 and html 6020 data in the session" in {
      val result = rentIncludeFixtureAndFittingsController6020().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html 6030 data in the session" in {
      val result = rentIncludeFixtureAndFittingsController6030().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML" in {
      val result = rentIncludeFixtureAndFittingsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html with none in the session" in {
      val result = rentIncludeFixtureAndFittingsNoController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and html with none in the session 6020" in {
      val result = rentIncludeFixtureAndFittingsController6030No().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentIncludeFixtureAndFittingsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
