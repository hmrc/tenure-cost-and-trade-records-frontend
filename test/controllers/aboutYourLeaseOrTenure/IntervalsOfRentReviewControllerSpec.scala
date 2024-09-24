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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.IntervalsOfRentReviewForm.intervalsOfRentReviewForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.*
import utils.TestBaseSpec

class IntervalsOfRentReviewControllerSpec extends TestBaseSpec {

  val test2001character =
    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibu"

  def intervalsOfRentReviewController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new IntervalsOfRentReviewController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      intervalsOfRentReviewView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "IntervalsOfRentReviewController GET /" should {
    "return 200 and HTML with vacant property start date present in session" in {
      val result = intervalsOfRentReviewController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val controller = intervalsOfRentReviewController(aboutLeaseOrAgreementPartTwo =
        Some(prefilledAboutLeaseOrAgreementPartTwoNoDate)
      )
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }

    "return 200 and HTML vacant property start date is none in session" in {
      val controller = intervalsOfRentReviewController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }

    "display the page with the fields prefilled in" when {
      "exists within the session" in {
        val result = intervalsOfRentReviewController().show()(fakeRequest)
        val html   = Jsoup.parse(contentAsString(result))
        Option(html.getElementById("nextReview.day").`val`()).value   shouldBe "1"
        Option(html.getElementById("nextReview.month").`val`()).value shouldBe "6"
        Option(html.getElementById("nextReview.year").`val`()).value  shouldBe "2022"
      }
    }
  }

  "IntervalsOfRentReviewController SUBMIT /" should {
    "throw a See_Other if an empty form is submitted" in {
      val res = intervalsOfRentReviewController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if intervalsOfRentReview is greater than max length is submitted" in {
      val res = intervalsOfRentReviewController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "intervalsOfRentReview" -> test2001character
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Form validation" should {

    val baseData: Map[String, String] = Map(
      "intervalsOfRentReview" -> "Intervals of rent review"
    )

    "error if intervalsOfRentReview is greater than max length" in {
      val formData = baseData + ("intervalsOfRentReview" -> test2001character)
      val form     = intervalsOfRentReviewForm(messages).bind(formData)
      mustContainError("intervalsOfRentReview", "error.intervalsOfRent.maxLength", form)
    }
  }
}
