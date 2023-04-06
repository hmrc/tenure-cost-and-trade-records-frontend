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

import navigation.AboutThePropertyNavigator
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty
import views.html.taskList

class CheckYourAnswersAboutThePropertyControllerSpec extends TestBaseSpec {

  val backLink: String = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val mockAboutThePropertyNavigator: AboutThePropertyNavigator                   = mock[AboutThePropertyNavigator]
  val mockCheckYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty =
    mock[checkYourAnswersAboutTheProperty]
  when(mockCheckYourAnswersAboutThePropertyView.apply(any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val mockTaskListView: taskList = mock[taskList]
  when(mockTaskListView.apply()(any, any)).thenReturn(HtmlFormat.empty)

  val checkYourAnswersAboutThePropertyController = new CheckYourAnswersAboutThePropertyController(
    stubMessagesControllerComponents(),
    mockCheckYourAnswersAboutThePropertyView,
    mockTaskListView,
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
}
