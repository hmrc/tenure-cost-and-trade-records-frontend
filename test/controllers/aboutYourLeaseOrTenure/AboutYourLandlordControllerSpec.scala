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

package controllers.aboutYourLeaseOrTenure

import connectors.addressLookup.*
import connectors.{Audit, MockAddressLookup}
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future
import scala.concurrent.Future.successful
import scala.language.reflectiveCalls

class AboutYourLandlordControllerSpec extends TestBaseSpec with MockAddressLookup:

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  val audit           = mock[Audit]
  given HeaderCarrier = any[HeaderCarrier]

  def aboutYourLandlordController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new AboutYourLandlordController(
    stubMessagesControllerComponents(),
    audit,
    aboutYourLeaseOrTenureNavigator,
    aboutYourLandlordView,
    addressLookupConnector,
    preEnrichedActionRefiner(
      forType = forType,
      aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne
    ),
    mockSessionRepo
  )

  "AboutYourLandlordController GET /" should {
    "return 200 and HTML with About Your Landlord in the session" in {
      val result = aboutYourLandlordController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with About Your Landlord in the session for 6020" in {
      val controller = aboutYourLandlordController(forType = FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TypeOfTenureController.show().url
      )
    }

    "return 200 and HTML when no About Your Landlord in the session" in {
      val controller = aboutYourLandlordController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = aboutYourLandlordController().show()(fakeRequestFromCYA)
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = aboutYourLandlordController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url + "#about-your-landlord")
    }
  }

  "AboutYourLandlordController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutYourLandlordController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
    "redirect to the next page if a valid form is submitted" in {
      when(addressLookupConnector.initJourney(any[AddressLookupConfig])(any))
        .thenReturn(successful(Some("/on-ramp")))
      val res = aboutYourLandlordController().submit(
        FakeRequest("POST", "/").withFormUrlEncodedBody(baseFormData.toSeq*)
      )
      status(res)                 shouldBe SEE_OTHER
      redirectLocation(res).value shouldBe "/on-ramp"
    }
  }

  "AddressLookup callback" should {
    "REDIRECT to next page" in {
      val lookup = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("1 Main Street", "Metropolis", "Gotham City")), Some("12345"), None),
        "auditRef",
        Some("id")
      )
      when(addressLookupConnector.getConfirmedAddress(any[String])).thenReturn(successful(lookup))
      val res    = aboutYourLandlordController().addressLookupCallback("123")(fakeRequest)
      status(res) shouldBe SEE_OTHER
    }

    "REDIRECT to CYA if come from CYA" in {
      val confirmedAddress = AddressLookupConfirmedAddress(
        AddressLookupAddress(Some(Seq("1 Main Street", "Metropolis", "Gotham City")), Some("12345"), None),
        "auditRef",
        Some("id")
      )
      given HeaderCarrier  = any[HeaderCarrier]
      when(addressLookupConnector.getConfirmedAddress(any[String])).thenReturn(successful(confirmedAddress))
      val res              = aboutYourLandlordController().addressLookupCallback("123")(fakeRequestFromCYA)
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show().url
      )
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
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val landlordFullName = "landlordFullName"
    }

    val baseFormData: Map[String, String] = Map(
      "landlordFullName" -> "Orinoco"
    )
  }
