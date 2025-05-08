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

package controllers.lettingHistory

import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.libs.json.Writes
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.hasPermanentResidents as HasPermanentResidentsView

class HasPermanentResidentsControllerSpec extends LettingHistoryControllerSpec:

  "the HasPermanentResidents controller" when {
    "the user has not provided any answer yet" should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.permanentResidents.heading"
        page.backLink          shouldBe controllers.routes.TaskListController.show().withFragment("letting-history").toString
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveNoneChecked
      }
      "be handling POST answer='yes' by replying 303 redirect to the 'PermanentResidentDetail' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value    shouldBe routes.ResidentDetailController.show().url
        verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
        hasPermanentResidents(data).value shouldBe true
      }
    }
    "the user has already answered"            should {
      "regardless of the given number of permanent residents" should {
        "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
          permanentResidents = oneResident
        ) {
          val result = controller.show(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF8
          val page = contentAsJsoup(result)
          page.radios("answer") shouldNot be(empty)
          page.radios("answer")    should haveChecked("yes")
          page.radios("answer") shouldNot haveChecked("no")
        }
        "be handling POST answer='yes' by replying 303 redirect to the 'PermanentResidentDetail' page" in new ControllerFixture(
          permanentResidents = oneResident
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value    shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value shouldBe true
          permanentResidents(data)          shouldBe oneResident
        }
        "be handling POST answer='no' by replying 303 redirect to the 'HasCompletedLettings' page" in new ControllerFixture(
          permanentResidents = fiveResidents,
          mayHaveMorePermanentResidents = Some(true)
        ) {
          // Answering 'no' will clear out all permanent residents
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "no"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value                                    shouldBe routes.HasCompletedLettingsController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value                                 shouldBe false
          permanentResidents(data)                                          shouldBe Nil
          mayHaveMoreEntitiesOf(kind = "permanentResidents", data.getValue) shouldBe None
        }
      }
      "and the maximum number of residents has been reached"  should {
        "be handling POST answer='yes' by replying 303 redirect to the 'PermanentResidentList' page" in new ControllerFixture(
          permanentResidents = fiveResidents
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value    shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
          hasPermanentResidents(data).value shouldBe true
          permanentResidents(data)            should have size 5
        }
      }
    }
    "regardless of the user providing answers" should {
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest
            .withFormUrlEncodedBody(
              "answer" -> "" // missing
            )
            .withQueryParams(
              "from" -> "CYA;some-fragment"
            )
            .withFragment(
              "permanent-residents"
            )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.backLink        shouldBe routes.CheckYourAnswersLettingHistoryController.show
          .withFragment("some-fragment")
          .toString
        page.error("answer") shouldBe "lettingHistory.hasPermanentResidents.required"
      }
    }
  }

  trait ControllerFixture(
    permanentResidents: List[ResidentDetail] = Nil,
    mayHaveMorePermanentResidents: Option[Boolean] = None
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HasPermanentResidentsController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HasPermanentResidentsView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(permanentResidents.nonEmpty),
            permanentResidents = permanentResidents,
            mayHaveMorePermanentResidents = mayHaveMorePermanentResidents
          )
        )
      ),
      repository
    )
