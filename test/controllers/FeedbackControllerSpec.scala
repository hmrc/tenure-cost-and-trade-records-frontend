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

package controllers

import actions.WithSessionRefiner
import config.ErrorHandler
import connectors.Audit
import org.jsoup.Jsoup
import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, defaultAwaitTimeout, redirectLocation, status}
import stub.StubSessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import utils.{HtmlAssertionHelper, TestBaseSpec}
import views.html.{confirmation, confirmationConnectionToProperty, confirmationNotConnected}
import views.html.feedback.{feedback, feedbackThx}

import scala.concurrent.{ExecutionContext, Future}

class FeedbackControllerSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with GuiceOneAppPerSuite
    with BeforeAndAfter
    with HtmlAssertionHelper
    with TestBaseSpec {

  private val sessionRepo = StubSessionRepo()

  override val fakeRequest = FakeRequest("GET", "/")
  private val postRequest  = FakeRequest("POST", "/")

  val auditServiceMock         = mock[Audit]
  val feedbackView: feedback   = app.injector.instanceOf[feedback]
  val feedbackThx: feedbackThx = app.injector.instanceOf[feedbackThx]

  private val controller =
    new FeedbackController(
      stubMessagesControllerComponents(),
      feedbackView,
      feedbackThx,
      confirmation,
      confirmationNotConnectedView,
      confirmationConnectionToProperty,
      WithSessionRefiner(inject[ErrorHandler], sessionRepo),
      inject[Audit]
    )

  before {
    reset(auditServiceMock)
  }

  "render feedback" should {
    "return HTML and 200 status" in {
      val result = controller.feedback()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "submit feedback" should {
    "call audit service and render feedbackThx" in {
      val result = controller.feedback()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "render thx" should {
    "return HTML and 200 status" in {
      val result = controller.feedbackThx()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "form is valid" when {
    "feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        //given
        val comments = "Really amazing bro, wow!"
        val rating   = "5"
        val result   = controller.feedbackSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        //then should be redirected to thx page
        status(of = result).shouldBe(Status.SEE_OTHER)
        redirectLocation(of = result).shouldBe(Some(controllers.routes.FeedbackController.feedbackThx.url))
      }
    }
  }

  "form is invalid" when {
    "feedback-rating is missing"    should {
      "fails with BAD_REQUEST" in {
        //given
        val comments = "Really amazing bro, wow!"
        val result   = controller.feedbackSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments
          )
        )
        //then should be redirected to thx page
        status(result) shouldBe Status.BAD_REQUEST
        val html     = Jsoup.parse(contentAsString(result))
        assertPageContainsElement(html, "feedback-rating-error")
      }
    }
    "feedback-comments is too long" should {
      "fails with BAD_REQUEST" in {
        //given
        val tooLongComment = (1 to 1200).toList.mkString("")
        val rating         = "5"
        val result         = controller.feedbackSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> tooLongComment,
            "feedback-rating"   -> rating
          )
        )
        //then should be redirected to thx page
        status(result) shouldBe Status.BAD_REQUEST
        val html           = Jsoup.parse(contentAsString(result))
        assertPageContainsElement(html, "feedback-comments-error")
      }
    }
  }
}
