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

package controllers

import connectors.Audit
import models.submissions.connectiontoproperty.StillConnectedDetails
import org.jsoup.Jsoup
import org.scalatest.BeforeAndAfter
import play.api.http.Status.*
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.{HtmlAssertionHelper, TestBaseSpec}

class FeedbackControllerSpec extends TestBaseSpec with BeforeAndAfter with HtmlAssertionHelper {

  private val postRequest = FakeRequest("POST", "/")

  private val auditServiceMock: Audit = mock[Audit]

  def feedbackController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new FeedbackController(
    stubMessagesControllerComponents(),
    feedbackView,
    feedbackThx,
    confirmation,
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
      val result = feedbackController().feedback(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some(UTF8)
    }

    "return HTML and 200 status for thank you page" in {
      val result = feedbackController().feedbackThx(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some(UTF8)
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
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
    "in page feedback is posted to audit"                  should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackSubmit(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
    "SUBMIT vacant property feedback empty"                should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res = feedbackController().feedbackSharedSubmit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }
    "main journey feedback is posted to audit"             should {
      "return redirect to thx page SEE_OTHER" in {
        val result = feedbackController().feedbackSharedSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
    "not connected feedback is posted to audit"            should {
      "return redirect to thx page SEE_OTHER" in {
        val controller = feedbackController(Some(prefilledStillConnectedDetailsNoToAll))
        val result     = controller.feedbackSharedSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
    "vacant property feedback is posted to audit"          should {
      "return redirect to thx page SEE_OTHER" in {
        val controller = feedbackController(Some(prefilledStillConnectedDetailsYesToAll))
        val result     = controller.feedbackSharedSubmit()(
          postRequest.withFormUrlEncodedBody(
            "feedback-comments" -> comments,
            "feedback-rating"   -> rating
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.FeedbackController.feedbackThx.url)
      }
    }
  }
  "feedbackSubmitWithoutSession" should {
    "return BadRequest for invalid form submission" in {
      val result = feedbackController().feedbackSubmitWithoutSession(
        postRequest.withFormUrlEncodedBody(
          "feedback-rating" -> ""
        )
      )
      status(result) shouldBe BAD_REQUEST
      contentType(result) shouldBe Some("text/html")
    }
    "return Redirect to feedbackThx for valid form submission" in {
      val result = feedbackController().feedbackSubmitWithoutSession(
        postRequest.withFormUrlEncodedBody(
          "feedback-comments" -> "Good feedback",
          "feedback-rating"   -> "5"
        )
      )
      status(result) shouldBe SEE_OTHER
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
        status(result) shouldBe BAD_REQUEST
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
        status(result) shouldBe BAD_REQUEST
        val html           = Jsoup.parse(contentAsString(result))
        assertPageContainsElement(html, "feedback-comments-error")
      }
    }
  }
}
