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

package controllers.lettingHistory

import models.Session
import models.submissions.common.{AnswerNo, AnswerYes}
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.residentDetail as ResidentDetailView

class ResidentDetailControllerSpec extends LettingHistoryControllerSpec:

  "the ResidentDetail controller" when {
    "the user session is fresh"                 should {
      "be handling GET /detail by replying 200 with the form showing name and address fields" in new FreshSessionFixture {
        val result  = controller.show(maybeIndex = None)(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include(s"""${routes.PermanentResidentsController.show.url}" class="govuk-back-link"""")
        content                     should include("""lettingHistory.residentDetail.heading""")
        content                     should include("""name="name"""")
        content                     should include("""name="address"""")
      }
      "be handling good POST /detail by replying 303 redirect to the 'Residents List' page" in new FreshSessionFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "name"    -> "Mr. Unknown",
          "address" -> "Neverland"
        )
        val result  = controller.submit(request)
        status(result)                             shouldBe SEE_OTHER
        redirectLocation(result).value             shouldBe routes.ResidentListController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        permanentResidentAt(data, index = 0).value shouldBe ResidentDetail(
          name = "Mr. Unknown",
          address = "Neverland"
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET /detail?index=0 by replying 200 with the form pre-filled with name and address values" in new StaleSessionFixture(
          oneResident
        ) {
          val result  = controller.show(maybeIndex = Some(0))(fakeGetRequest)
          val content = contentAsString(result)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          content                     should include("Mr. One")
          content                     should include("Address One")
        }
        "be handling POST /detail?unknown by replying 303 redirect to 'Residents List' page" in new StaleSessionFixture(
          oneResident
        ) {
          // Post an unknown resident detail and expect it to become the third resident
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"    -> "Mr. Unknown",
            "address" -> "Neverland"
          )
          val result  = controller.submit(request)
          status(result)                                     shouldBe SEE_OTHER
          redirectLocation(result).value                     shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          permanentResidents(data)                             should have size 2 // instead of 1
          permanentResidentAt(data, index = 0).value         shouldBe oneResident.head
          permanentResidentAt(data, index = 1).value.name    shouldBe "Mr. Unknown"
          permanentResidentAt(data, index = 1).value.address shouldBe "Neverland"
        }
        "be handling POST /detail?overwrite by replying 303 redirect to 'Residents List' page" in new StaleSessionFixture(
          twoResidents
        ) {
          // Post the second resident detail again and expect it to be changed
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"    -> twoResidents.last.name,
            "address" -> "22, Different Street"
          )
          val result  = controller.submit(request)
          status(result)                                     shouldBe SEE_OTHER
          redirectLocation(result).value                     shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          permanentResidents(data)                             should have size 2 // the same as it was before sending the post request
          permanentResidentAt(data, index = 0).value         shouldBe oneResident.head
          permanentResidentAt(data, index = 1).value.name    shouldBe "Mr. Two"
          permanentResidentAt(data, index = 1).value.address shouldBe "22, Different Street"
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling GET /detail by replying 303 redirect to the 'Residents List' page" in new StaleSessionFixture(
          fiveResidents
        ) {
          val result = controller.show(maybeIndex = None)(fakeGetRequest)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.ResidentListController.show.url
        }
      }
    }
    "the user session is either fresh or stale" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new FreshSessionFixture {
        val result  = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "name"    -> "",
            "address" -> ""
          )
        )
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("lettingHistory.residentDetail.name.required")
        content          should include("lettingHistory.residentDetail.address.required")
      }
    }
  }

  // It provides the scenario of fresh session (there's no letting history yet in session)
  trait FreshSessionFixture extends MockRepositoryFixture with SessionCapturingFixture:
    val controller = new ResidentDetailController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[ResidentDetailView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = None
      ),
      repository
    )

  // It represents the scenario of ongoing session (with some letting history already created)
  trait StaleSessionFixture(permanentResidents: List[ResidentDetail])
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new ResidentDetailController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[ResidentDetailView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(if permanentResidents.isEmpty then AnswerNo else AnswerYes),
            permanentResidents = permanentResidents
          )
        )
      ),
      repository
    )
