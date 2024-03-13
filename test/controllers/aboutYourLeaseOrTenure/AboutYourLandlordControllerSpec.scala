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

  val mockNavigator              = mock[AboutYourLeaseOrTenureNavigator]
  val mockAddressLookupConnector = mock[AddressLookupConnector]
  val errorHandler: ErrorHandler = inject[ErrorHandler]
  def aboutYourLandlordController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  )                              =
    new AboutYourLandlordController(
      stubMessagesControllerComponents(),
      mockNavigator,
      aboutYourLandlordView,
      mockAddressLookupConnector,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      errorHandler,
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      when(mockNavigator.from(any[Request[AnyContent]])).thenReturn("")
      val result = aboutYourLandlordController().show(fakeRequest)
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

  "Additional information form" should {
    "error if buildingNameNumber is missing" in {
      val formDataWithEmptyBuildingNameNumber = baseFormData.updated(TestData.errorKey.buildingNameNumber, "")
      val form                                = aboutTheLandlordForm.bind(formDataWithEmptyBuildingNameNumber)

      mustContainError(errorKey.buildingNameNumber, "error.buildingNameNumber.required", form)
    }

    "error if town is missing" in {
      val formDataWithEmptyTown = baseFormData.updated(TestData.errorKey.town, "")
      val form                  = aboutTheLandlordForm.bind(formDataWithEmptyTown)

      mustContainError(errorKey.town, "error.townCity.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val buildingNameNumber = "landlordAddress.buildingNameNumber"
      val town               = "landlordAddress.town"
      val postcode           = "alternativelandlordAddressContactAddress.postcode"
    }

    val tooLongEmail                      = "email_too_long_for_validation_againt_business_rules_specify_but_DB_constraints@something.co.uk"
    val baseFormData: Map[String, String] = Map(
      "landlordAddress.postcode" -> "BN12 4AX"
    )
  }
}
