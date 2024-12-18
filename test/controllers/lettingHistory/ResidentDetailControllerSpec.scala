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
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.residentDetail as ResidentDetailView

class ResidentDetailControllerSpec extends LettingHistoryControllerSpec:

  "the ResidentDetail controller" when {
    "the user session is fresh"                        should {
      "be handling GET /detail by replying 200 with the form showing name and address fields" in new ControllerFixture {
        val result = controller.show(maybeIndex = None)(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.residentDetail.heading"
        page.backLink          shouldBe routes.HasPermanentResidentsController.show.url
        page.input("name")       should beEmpty
        page.textarea("address") should beEmpty
      }
      "be handling good POST /detail by replying 303 redirect to the 'Residents List' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "name"    -> "Mr. Unknown",
          "address" -> "Neverland"
        )
        val result  = controller.submit(request)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.ResidentListController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        permanentResidents(data)         should have size 1
        permanentResidents(data)(0)    shouldBe ResidentDetail(
          name = "Mr. Unknown",
          address = "Neverland"
        )
      }
    }
    "the user session is stale" when {
      "regardless of the given number residents"             should {
        "be handling GET /detail?index=0 by replying 200 with the form pre-filled with name and address values" in new ControllerFixture(
          oneResident
        ) {
          val result = controller.show(maybeIndex = Some(0))(fakeGetRequest)
          status(result)            shouldBe OK
          contentType(result).value shouldBe HTML
          charset(result).value     shouldBe UTF_8.charset
          val page = contentAsJsoup(result)
          page.input("name") should haveValue("Mr. One")
          // TODO page.textarea("address")   should haveValue("Address One")
        }
        "be handling POST /detail?unknown by replying 303 redirect to 'Residents List' page" in new ControllerFixture(
          oneResident
        ) {
          // Post an unknown resident detail and expect it to become the third resident
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"    -> "Mr. Unknown",
            "address" -> "Neverland"
          )
          val result  = controller.submit(request)
          status(result)                      shouldBe SEE_OTHER
          redirectLocation(result).value      shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          permanentResidents(data)              should have size 2 // instead of 1
          permanentResidents(data)(0)         shouldBe oneResident.head
          permanentResidents(data)(1).name    shouldBe "Mr. Unknown"
          permanentResidents(data)(1).address shouldBe "Neverland"
        }
        "be handling POST /detail?overwrite by replying 303 redirect to 'Residents List' page" in new ControllerFixture(
          twoResidents
        ) {
          // Post the second resident detail again and expect it to be changed
          val request = fakePostRequest.withFormUrlEncodedBody(
            "name"    -> twoResidents.last.name,
            "address" -> "22, Different Street"
          )
          val result  = controller.submit(request)
          status(result)                      shouldBe SEE_OTHER
          redirectLocation(result).value      shouldBe routes.ResidentListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
          permanentResidents(data)              should have size 2 // the same as it was before sending the post request
          permanentResidents(data)(0)         shouldBe oneResident.head
          permanentResidents(data)(1).name    shouldBe "Mr. Two"
          permanentResidents(data)(1).address shouldBe "22, Different Street"
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling GET /detail by replying 303 redirect to the 'Residents List' page" in new ControllerFixture(
          fiveResidents
        ) {
          val result = controller.show(maybeIndex = None)(fakeGetRequest)
          status(result)                 shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe routes.ResidentListController.show.url
        }
      }
    }
    "regardless of what the user might have submitted" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "name"    -> "",
            "address" -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("name")    shouldBe "lettingHistory.residentDetail.name.required"
        page.error("address") shouldBe "lettingHistory.residentDetail.address.required"
      }
    }
  }

  trait ControllerFixture(permanentResidents: List[ResidentDetail] = Nil)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new ResidentDetailController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[ResidentDetailView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasPermanentResidents = Some(permanentResidents.nonEmpty),
            permanentResidents = permanentResidents
          )
        )
      ),
      repository
    )
