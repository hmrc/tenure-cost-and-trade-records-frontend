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

import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{AdvertisingDetail, IntendedDetail, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.hasOnlineAdvertising as HasOnlineAdvertisingView

import scala.language.implicitConversions

class HasOnlineAdvertisingSpec extends LettingHistoryControllerSpec:

  "the HasOnlineAdvertising controller" when {
    "the user session is fresh"                should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new ControllerFixture(
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.hasOnlineAdvertising.heading"
        page.backLink          shouldBe routes.IsYearlyAvailableController.show.url
        page.radios("answer") shouldNot be(empty)
        page.radios("answer")    should haveNoneChecked
      }
      "be handling POST answer='yes' and reply 303 redirect to the 'OnlineAdvertisingDetail' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value   shouldBe routes.AdvertisingDetailController.show().url
        verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
        hasOnlineAdvertising(data).value shouldBe true
      }
    }
    "the user has already answered"            should {
      "regardless of the given number of online advertising" should {
        "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
          onlineAdvertising = oneAdvertising
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
        "be handling POST answer='yes' and reply 303 redirect to the 'OnlineAdvertisingDetail' page" in new ControllerFixture(
          onlineAdvertising = oneAdvertising
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.AdvertisingListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasOnlineAdvertising(data).value shouldBe true
          onlineAdvertising(data)          shouldBe oneAdvertising
        }
        "be handling POST answer='no' and reply 303 redirect to the 'CheckYourAnswers' page" in new ControllerFixture(
          onlineAdvertising = fiveAdvertisings,
          mayHaveMoreOnlineAdvertising = Some(true)
        ) {
          // Answering 'no' will clear out all online advertising
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "no"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value                                   shouldBe routes.CheckYourAnswersLettingHistoryController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasOnlineAdvertising(data).value                                 shouldBe false
          onlineAdvertising(data)                                          shouldBe Nil
          mayHaveMoreEntitiesOf(kind = "onlineAdvertising", data.getValue) shouldBe None
        }
      }
      "and the maximum number of residents has been reached" should {
        "be handling POST answer='yes' by replying 303 redirect to the 'OnlineAdvertisingList' page" in new ControllerFixture(
          onlineAdvertising = fiveAdvertisings
        ) {
          val result = controller.submit(
            fakePostRequest.withFormUrlEncodedBody(
              "answer" -> "yes"
            )
          )
          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value   shouldBe routes.AdvertisingListController.show.url
          verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
          hasOnlineAdvertising(data).value shouldBe true
          onlineAdvertising(data)            should have size 5
        }
      }
    }
    "regardless of the user providing answers" should {
      "be handling invalid POST answer=null and reply 400 with error message" in new ControllerFixture(
        isYearlyAvailable = Some(false)
      ) {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.backLink        shouldBe routes.TradingSeasonController.show.url
        page.error("answer") shouldBe "lettingHistory.hasOnlineAdvertising.required"
      }
    }
  }

  trait ControllerFixture(
    isYearlyAvailable: Option[Boolean] = None,
    onlineAdvertising: List[AdvertisingDetail] = List.empty,
    mayHaveMoreOnlineAdvertising: Option[Boolean] = None
  ) extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new HasOnlineAdvertisingController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[HasOnlineAdvertisingView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Option(
          LettingHistory(
            intendedLettings = Some(
              IntendedDetail(
                isYearlyAvailable = isYearlyAvailable
              )
            ),
            hasOnlineAdvertising = if onlineAdvertising.isEmpty then None else Some(true),
            onlineAdvertising = onlineAdvertising,
            mayHaveMoreOnlineAdvertising = mayHaveMoreOnlineAdvertising
          )
        )
      ),
      repository
    )
