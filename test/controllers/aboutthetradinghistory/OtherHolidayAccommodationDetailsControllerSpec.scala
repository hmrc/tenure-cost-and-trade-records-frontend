/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.aboutthetradinghistory

import controllers.aboutthetradinghistory
import form.aboutthetradinghistory.OtherHolidayAccommodationDetailsForm.otherHolidayAccommodationDetailsForm
import models.Session
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import models.submissions.common.AnswerNo
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future
import scala.language.reflectiveCalls

class OtherHolidayAccommodationDetailsControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  private val nextPage = aboutthetradinghistory.routes.GrossReceiptsLettingUnitsController.show().url
  def otherHolidayAccommodationDetailsController(
    aboutTheTradingHistoryPartOne: Option[AboutTheTradingHistoryPartOne] = Some(
      prefilledAboutTheTradingHistoryPartOne
    )
  ) = new OtherHolidayAccommodationDetailsController(
    stubMessagesControllerComponents(),
    aboutYourTradingHistoryNavigator,
    otherHolidayAccommodationDetailsView,
    preEnrichedActionRefiner(aboutTheTradingHistoryPartOne = aboutTheTradingHistoryPartOne),
    mockSessionRepo
  )

  "OtherHolidayAccommodationDetailsController GET /" should {
    "return 200 and HTML" in {
      val result = otherHolidayAccommodationDetailsController().show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML if the session is empty" in {
      val controller = otherHolidayAccommodationDetailsController(aboutTheTradingHistoryPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = otherHolidayAccommodationDetailsController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.aboutthetradinghistory.routes.OtherHolidayAccommodationController.show().url
      )
    }

  }

  "OtherHolidayAccommodationDetailsController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = otherHolidayAccommodationDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
    "save the form data and redirect to the next page on answer No with weeks field filled" in {
      val res = otherHolidayAccommodationDetailsController().submit(
        fakePostRequest
          .withFormUrlEncodedBody("otherHolidayAccommodationOpenAllYear" -> AnswerNo.name, "weeksOpen" -> "33")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "handle save as draft and redirect to the draft page" in {
      when(mockSessionRepo.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier]))
        .thenReturn(Future.successful(()))
      val saveAsDraftUri  = "/test-uri"
      val saveAsDraftPage = controllers.routes.SaveAsDraftController.customPassword(saveAsDraftUri).url

      val res = otherHolidayAccommodationDetailsController().submit(
        FakeRequest("POST", saveAsDraftUri).withFormUrlEncodedBody(
          "otherHolidayAccommodationOpenAllYear" -> AnswerNo.name,
          "weeksOpen"                            -> "33",
          "save_button"                          -> "save_button"
        )
      )

      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(saveAsDraftPage)
    }
  }

  "OtherHolidayAccommodationDetails" should {
    "error if otherHolidayAccommodationOpenAllYear answer is missing" in {
      val formData = baseFormData - errorKey.otherHolidayAccommodationOpenAllYear
      val form     = otherHolidayAccommodationDetailsForm.bind(formData)

      mustContainError(
        errorKey.otherHolidayAccommodationOpenAllYear,
        "error.otherHolidayAccommodationOpenAllYear.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val otherHolidayAccommodationOpenAllYear: String = "otherHolidayAccommodationOpenAllYear"
    }

    val baseFormData: Map[String, String] = Map("otherHolidayAccommodationOpenAllYear" -> "yes")
  }

}
