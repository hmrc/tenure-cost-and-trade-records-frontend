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

package controllers.aboutfranchisesorlettings

import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.JsoupHelpers.*
import utils.TestBaseSpec

import scala.concurrent.Future.successful

class AddAnotherLettingOtherPartOfPropertyControllerSpec extends TestBaseSpec {

  "the AddAnotherLettingOtherPartOfProperty controller" when {
    "handling GET / requests"    should {
      "reply 200 with the fresh form if given index does not exist" in new ControllerFixture {
        val result = controller.show(9)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        contentAsString(result)     should not include "checked"
      }
      "reply 200 with the pre-filled form if given index exists" in new ControllerFixture {
        val result = controller.show(0)(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()                         shouldBe "addAnotherLettingOtherPartOfProperty.heading"
        html.getElementById("addAnotherLettingOtherPartOfProperty").toString should include("""value="yes" checked>""")
        html.backLinkHref                                                  shouldBe routes.LettingOtherPartOfPropertyRentIncludesController.show(0).url
      }
      "reply 200 with the pre-filled form if from=CYA" in new ControllerFixture {
        val result = controller.show(1)(fakeRequestFromCYA)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val html = contentAsJsoup(result)
        html.getElementsByTag("h1").first().text()                           shouldBe "addAnotherLettingOtherPartOfProperty.heading"
        html.getElementById("addAnotherLettingOtherPartOfProperty-2").toString should include("""value="no" checked>""")
        html.backLinkHref                                                    shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }
    }
    "handling POST / requests"   should {
      "reply 303 redirect to 'MaxOfLettingsReached' if lettingCurrentIndex >= 4" in new ControllerFixture(
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettings.copy(lettingCurrentIndex = 5)
        )
      ) {
        val result = controller.submit(0)(fakePostRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe controllers.routes.MaxOfLettingsReachedController
          .show(Some("franchiseLetting"))
          .url
      }
      "reply 400 and error messages if the form is submitted with invalid data" in new ControllerFixture {
        val result  = controller.submit(0)(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("error.addAnotherLetting.required")
        reset(repository)
      }
      "reply 303 redirect to 'LettingOtherPartOfProperty' if answer=yes and lettingSections was empty" in new ControllerFixture(
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettings.copy(lettingSections = IndexedSeq.empty)
        )
      ) {
        val result =
          controller.submit(0)(fakePostRequest.withFormUrlEncodedBody("addAnotherLettingOtherPartOfProperty" -> "yes"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.LettingOtherPartOfPropertyDetailsController.show().url
      }
      "reply 303 redirect to 'CheckYourAnswersAboutFranchiseOrLettings' if answer=no and lettingSections was empty" in new ControllerFixture(
        aboutFranchisesOrLettings = Some(
          prefilledAboutFranchiseOrLettings.copy(lettingSections = IndexedSeq.empty)
        )
      ) {
        val result =
          controller.submit(0)(fakePostRequest.withFormUrlEncodedBody("addAnotherLettingOtherPartOfProperty" -> "no"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      }
      "reply 303 redirect to 'LettingOtherPartOfPropertyDetails' if answer=yes and lettingSections has some" in new ControllerFixture {
        val result =
          controller.submit(0)(fakePostRequest.withFormUrlEncodedBody("addAnotherLettingOtherPartOfProperty" -> "yes"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.LettingOtherPartOfPropertyDetailsController.show(Some(1)).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        data.getValue.aboutFranchisesOrLettings.value
          .lettingSections(0)
          .addAnotherLettingToProperty
          .value                         should beAnswerYes
        reset(repository)
      }
    }
    "handling REMOVE / requests" should {
      "reply 303 redirect to 'AddAnotherLettingOtherPartOfProperty' if given index does not exist" in new ControllerFixture {
        val result = controller.remove(99)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
      }
      "reply 200 with the 'Confirm remove' page" in new ControllerFixture {
        val result = controller.remove(0)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val content = contentAsString(result)
        content should include("""genericRemoveConfirmation.heading""")
        content should include(
          s"""action="${routes.AddAnotherLettingOtherPartOfPropertyController.performRemove(0)}""""
        )
      }
      "reply 400 with the 'Confirm remove' page if missing answer" in new ControllerFixture {
        val result  = controller.performRemove(0)(
          fakePostRequest.withFormUrlEncodedBody(
            "genericRemoveConfirmation" -> "" // missing answer
          )
        )
        status(result) shouldBe BAD_REQUEST
        val content = contentAsString(result)
        content should include("""genericRemoveConfirmation.heading""")
        content should include("""error.confirmableAction.required""")
      }
      "reply 303 redirect to 'Confirm remove' page if missing answer and missing aboutFranchisesOrLettings" in new ControllerFixture(
        aboutFranchisesOrLettings = None
      ) {
        val result = controller.performRemove(0)(
          fakePostRequest.withFormUrlEncodedBody(
            "genericRemoveConfirmation" -> "" // missing answer
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
      }
      "reply 303 redirect to 'AddAnotherLettingOtherPartOfProperty' page if answer='yes'" in new ControllerFixture() {
        val result = controller.performRemove(0)(
          fakePostRequest.withFormUrlEncodedBody(
            "genericRemoveConfirmation" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
      }
      "reply 303 redirect to 'AddAnotherLettingOtherPartOfProperty' page if answer='no'" in new ControllerFixture() {
        val result = controller.performRemove(0)(
          fakePostRequest.withFormUrlEncodedBody(
            "genericRemoveConfirmation" -> "no"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AddAnotherLettingOtherPartOfPropertyController.show(0).url
      }
    }
  }

  trait ControllerFixture(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ):
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

    val controller =
      new AddAnotherLettingOtherPartOfPropertyController(
        stubMessagesControllerComponents(),
        aboutFranchisesOrLettingsNavigator,
        addAnotherOperationConcessionFranchise,
        genericRemoveConfirmationView,
        preEnrichedActionRefiner(
          aboutFranchisesOrLettings = aboutFranchisesOrLettings
        ),
        repository
      )

//
//  import TestData._

//
//    "REMOVE /" should {
//      "redirect if an empty form is submitted" in {
//        val result = controller.remove(1)(fakeRequest)
//        status(result) shouldBe SEE_OTHER
//      }
//    }
//  }
//
//  "Add another letting accommodation form" should {
//    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
//      val formData = baseFormData - errorKey.addAnotherLettingOtherPartOfProperty
//      val form     = addAnotherLettingForm.bind(formData)
//
//      mustContainError(errorKey.addAnotherLettingOtherPartOfProperty, "error.addAnotherLetting.required", form)
//    }
//  }
//
//  "Remove catering operation" should {
//    "render the removal confirmation page on remove" in {
//      val controller     = controller
//      val idxToRemove    = 0
//      val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6010YesSession, fakeRequest)
//      val result         = controller.remove(idxToRemove)(sessionRequest)
//      status(result)      shouldBe OK
//      contentType(result) shouldBe Some("text/html")
//    }
//
//    "handle form submission with 'Yes' and perform removal" in {
//      val controller      = controller
//      val idxToRemove     = 0
//      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
//      val sessionRequest  = SessionRequest(sessionAboutFranchiseOrLetting6010YesSession, requestWithForm)
//      val result          = controller.performRemove(idxToRemove)(sessionRequest)
//      status(result) shouldBe BAD_REQUEST
//
//    }
//
//    "handle form submission with 'No' and cancel removal" in {
//      val controller      = controller
//      val idxToRemove     = 0
//      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
//      val result          = controller.performRemove(idxToRemove)(requestWithForm)
//      status(result) shouldBe BAD_REQUEST
//    }
//
//  }
//  object TestData {
//    val errorKey: ErrorKey = new ErrorKey
//
//    class ErrorKey {
//      val addAnotherLettingOtherPartOfProperty: String =
//        "addAnotherLettingOtherPartOfProperty"
//    }
//
//    val baseFormData: Map[String, String] = Map("addAnotherLettingOtherPartOfProperty" -> "yes")
//  }
}
