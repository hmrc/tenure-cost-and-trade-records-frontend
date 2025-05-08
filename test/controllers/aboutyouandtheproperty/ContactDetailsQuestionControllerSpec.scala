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

package controllers.aboutyouandtheproperty

import connectors.{Audit, MockAddressLookup}
import models.Session
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AlternativeAddress, ContactDetailsQuestion}
import models.submissions.common.AnswerYes
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import repositories.SessionRepo
import utils.{JsoupHelpers, TestBaseSpec}

import scala.concurrent.Future.successful

class ContactDetailsQuestionControllerSpec extends TestBaseSpec with JsoupHelpers:

  trait ControllerFixture(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) extends MockAddressLookup:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any, any)).thenReturn(successful(()))

    val controller =
      new ContactDetailsQuestionController(
        stubMessagesControllerComponents(),
        mock[Audit],
        aboutYouAndThePropertyNavigator,
        contactDetailsQuestionView,
        preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
        addressLookupConnector,
        repository
      )

  "the ContactDetailsQuestion controller" when {
    "handling GET /"                   should {
      "reply 200 with an empty form" in new ControllerFixture {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                        shouldBe "contactDetailsQuestion.heading"
        page.backLink                       shouldBe routes.AboutYouController.show().url
        page.radios("contactDetailsQuestion") should haveNoneChecked
      }
      "reply 200 with a pre-filled form" in new ControllerFixture(
        aboutYouAndTheProperty =
          Some(prefilledAboutYouAndThePropertyYes.copy(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerYes))))
      ) {
        val result = controller.show(fakeRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading                        shouldBe "contactDetailsQuestion.heading"
        page.backLink                       shouldBe routes.AboutYouController.show().url
        page.radios("contactDetailsQuestion") should haveChecked("yes")
      }

      "GET / return HTML" in new ControllerFixture {
        val result = controller.show(fakeRequest)
        contentType(result)     shouldBe Some("text/html")
        Helpers.charset(result) shouldBe Some("utf-8")
      }

      "GET / return 200 no contact details in the session" in new ControllerFixture(aboutYouAndTheProperty = None) {
        val result = controller.show(fakeRequest)
        status(result)          shouldBe OK
        contentType(result)     shouldBe Some("text/html")
        Helpers.charset(result) shouldBe Some("utf-8")
      }

      "return correct backLink when 'from=TL' query param is present" in new ControllerFixture {
        val result = controller.show()(FakeRequest(GET, "/path?from=TL"))
        contentAsString(result) should include(controllers.routes.TaskListController.show().url)
      }

      "SUBMIT /" should {
        "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture {
          val res = controller.submit(
            FakeRequest().withFormUrlEncodedBody(Seq.empty*)
          )
          status(res) shouldBe BAD_REQUEST
        }
      }

      "Redirect when form data submitted" in new ControllerFixture {
        val res = controller.submit(
          FakeRequest(POST, "/").withFormUrlEncodedBody(
            "contactDetailsQuestion" -> "yes"
          )
        )
        status(res) shouldBe SEE_OTHER
      }
    }
    "handling POST /"                  should {
      "reply 404 if the submitted data is invalid" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "contactDetailsQuestion" -> "" // missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("contactDetailsQuestion") shouldBe "error.contactDetailsQuestion.missing"
      }
      "reply 303 redirect to the address lookup page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "contactDetailsQuestion" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/on-ramp"
        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session.capture())(using any, any)
        session.getValue.aboutYouAndTheProperty.value.altDetailsQuestion.value.contactDetailsQuestion shouldBe AnswerYes
      }
    }
    "retrieving the confirmed address" should {
      "reply 303 redirect to the next page" in new ControllerFixture {
        val result = controller.addressLookupCallback("confirmedAddress")(fakeRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.AlternativeContactDetailsController.show().url

        val id = captor[String]
        verify(addressLookupConnector, once).getConfirmedAddress(id)(using any)
        id.getValue shouldBe "confirmedAddress"

        val session = captor[Session]
        verify(repository, once).saveOrUpdate(session)(using any, any)
        session.getValue.aboutYouAndTheProperty.value.altContactInformation.value.alternativeContactAddress shouldBe AlternativeAddress(
          buildingNameNumber = addressLookupConfirmedAddress.address.lines.get.head,
          street1 = Some(addressLookupConfirmedAddress.address.lines.get.apply(1)),
          town = addressLookupConfirmedAddress.address.lines.get.last,
          county = None,
          postcode = addressLookupConfirmedAddress.address.postcode.get
        )
      }
    }
  }
