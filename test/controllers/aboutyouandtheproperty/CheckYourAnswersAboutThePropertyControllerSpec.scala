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

package controllers.aboutyouandtheproperty

import navigation.AboutYouAndThePropertyNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty

class CheckYourAnswersAboutThePropertyControllerSpec extends TestBaseSpec {

  val backLink: String = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val mockAboutThePropertyNavigator: AboutYouAndThePropertyNavigator             = mock[AboutYouAndThePropertyNavigator]
  val mockCheckYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty =
    mock[checkYourAnswersAboutTheProperty]
  when(mockCheckYourAnswersAboutThePropertyView.apply(any, any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val checkYourAnswersAboutThePropertyController = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    mockCheckYourAnswersAboutThePropertyView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersAboutThePropertyController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutThePropertyController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutThePropertyController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
