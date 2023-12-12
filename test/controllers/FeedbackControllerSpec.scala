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

import connectors.Audit
import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import utils.{HtmlAssertionHelper, TestBaseSpec}

class FeedbackControllerSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with GuiceOneAppPerSuite
    with BeforeAndAfter
    with HtmlAssertionHelper
    with TestBaseSpec {

  private val postRequest = FakeRequest("POST", "/")

  private val auditServiceMock: Audit = mock[Audit]

  def feedbackController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new FeedbackController(
    stubMessagesControllerComponents(),
    feedbackView,
    feedbackThx,
    confirmation,
    confirmationNotConnectedView,
    confirmationConnectionToProperty,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    auditServiceMock
  )

  val comments = "Really amazing bro, wow!"
  val rating   = "5"

  before {
    reset(auditServiceMock)
  }

  "Feedback controller" should {
    "return HTML and 200 status for feedback form page" in {
      val result = feedbackController().feedback()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML and 200 status for thank you page" in {
      val result = feedbackController().feedbackThx()(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "form is valid" when {
    "request reference number feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackRequestReferenceNumber()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }

    "in page feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }

    "connected feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackConnectedSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }

    "vacant property feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackVacantPropertySubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }

    "not connected feedback is posted to audit" should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackNotConnectedSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
  }

  "feedbackSubmitWithoutSession" should {
    "return BadRequest for invalid form submission" in {
      val result = feedbackController().feedbackSubmitWithoutSession()(
        postRequest.withFormUrlEncodedBody(
          "feedback-rating" -> ""
        )
      )
      status(result) shouldBe Status.BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
    }

    "return Redirect to feedbackThx for valid form submission" in {
      val result = feedbackController().feedbackSubmitWithoutSession()(
        postRequest.withFormUrlEncodedBody(
          "feedback-comments" -> "Good feedback",
          "feedback-rating"   -> "5"
        )
      )
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
    }
  }

  "form is invalid" when {
    "feedback-rating is missing" should {
      "fails with BAD_REQUEST" in {
        val result = feedbackController().feedbackRequestReferenceNumber()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments
          )
        )
        status(result) shouldBe Status.BAD_REQUEST
        val html   = Jsoup.parse(contentAsString(result))
        assertPageContainsElement(html, "feedback-rating-error")
      }
    }

    "feedback-comments is too long" should {
      "fails with BAD_REQUEST" in {
        val tooLongComment = (1 to 1200).toList.mkString("")
        val result         = feedbackController().feedbackRequestReferenceNumber()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> tooLongComment,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe Status.BAD_REQUEST
        val html           = Jsoup.parse(contentAsString(result))
        assertPageContainsElement(html, "feedback-comments-error")
      }
    }
  }
}
