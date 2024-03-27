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

import config.ErrorHandler
import connectors.AddressLookupConnector
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.{Address, AddressLookup}
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.mvc.{AnyContent, Request}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future

class AboutYourLandlordControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val mockNavigator: AboutYourLeaseOrTenureNavigator     = mock[AboutYourLeaseOrTenureNavigator]
  val mockAddressLookupConnector: AddressLookupConnector = mock[AddressLookupConnector]
  val errorHandler: ErrorHandler                         = inject[ErrorHandler]

  def aboutYourLandlordController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new AboutYourLandlordController(
    stubMessagesControllerComponents(),
    mockNavigator,
    aboutYourLandlordView,
    mockAddressLookupConnector,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    errorHandler,
    mockSessionRepo
  )

  def aboutYourLandlordController6020(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new AboutYourLandlordController(
    stubMessagesControllerComponents(),
    mockNavigator,
    aboutYourLandlordView,
    mockAddressLookupConnector,
    preEnrichedActionRefiner(forType = forType6020, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    errorHandler,
    mockSessionRepo
  )

  def aboutYourLandlordControllerNone = new AboutYourLandlordController(
    stubMessagesControllerComponents(),
    mockNavigator,
    aboutYourLandlordView,
    mockAddressLookupConnector,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    errorHandler,
    mockSessionRepo
  )

  "AboutYourLandlordController GET /" should {
    "return 200 with data in session" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML with data in session" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML for 6020" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordController6020().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML for empty session" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val res = aboutYourLandlordController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "AddressLookup callback" should {
    "REDIRECT to next page" in {
      val lookup = AddressLookup(
        Some(Address(Some(Seq("1 Main Street", "Metropolis", "Gotham City")), Some("12345"), None)),
        Some("auditRef"),
        Some("id")
      )
      when(mockAddressLookupConnector.getAddress(any[String])(any[HeaderCarrier])).thenReturn(Future.successful(lookup))
      val res    = aboutYourLandlordController().addressLookupCallback("123")(fakeRequest)
      status(res) shouldBe SEE_OTHER
    }
  }

  "Landlord name form" should {
    "error if landlord name is missing" in {
      val formDataWithEmptyLandlordFullName = baseFormData.updated(TestData.errorKey.landlordFullName, "")
      val form                              = aboutTheLandlordForm.bind(formDataWithEmptyLandlordFullName)

      mustContainError(errorKey.landlordFullName, "error.landlordFullName.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val landlordFullName = "landlordFullName"
    }

    val baseFormData: Map[String, String] = Map(
      "landlordFullName" -> "Orinoco"
    )
  }
}
