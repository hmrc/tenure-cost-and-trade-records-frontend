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
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import org.mockito.ArgumentCaptor
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import views.html.lettingHistory.residentDetail as ResidentDetailView

import scala.concurrent.Future.successful

class ResidentDetailControllerSpec extends TestBaseSpec:

  "the ResidentDetail controller" when {
    "the user session is fresh"                 should {
      "be handling GET /detail by replying 200 with the form showing name and address fields" in new FreshSessionFixture {
        val result  = controller.show()(fakeGetRequest)
        val content = contentAsString(result)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        content                     should include("""name="name"""")
        content                     should include("""name="address"""")
      }
      "be handling good POST /detail by replying 303 redirect to to 'Residents List' page" in new FreshSessionFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "name"    -> "Mr. Peter Pan",
          "address" -> "20, Fantasy Street, Birds' Island, BIR067"
        )
        val result  = controller.submit(request)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/path/to/resident-list"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        residentDetail(data).value     shouldBe ResidentDetail(
          name = "Mr. Peter Pan",
          address = "20, Fantasy Street, Birds' Island, BIR067"
        )
      }
    }
    "the user session is either fresh or stale" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new FreshSessionFixture {
        val result  = controller.submit(fakePostRequest)
        val content = contentAsString(result)
        status(result) shouldBe BAD_REQUEST
        content          should include("lettingHistory.residentDetail.name.error")
        content          should include("lettingHistory.residentDetail.address.error")
      }
    }
  }

  trait MockRepositoryFixture:
    val repository = mock[SessionRepo]
    val data       = captor[Session]
    when(repository.saveOrUpdate(any[Session])(any[Writes[Session]], any[HeaderCarrier])).thenReturn(successful(()))

  trait SessionCapturingFixture:
    def residentDetail(session: ArgumentCaptor[Session]): Option[ResidentDetail] =
      LettingHistory.residentDetail(session.getValue)

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
