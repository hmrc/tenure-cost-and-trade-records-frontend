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

package controllers

import models.Session
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.Identifier
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.{AnyContent, Call, Request}
import play.api.test.Helpers.{contentAsString, contentType, header, status, stubMessagesControllerComponents}
import play.api.test.{DefaultAwaitTimeout, FakeRequest, Helpers}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{MockitoExtendedSugar, TestBaseSpec}

trait MaxOfLettingsReachedControllerBehaviours:
  this: AnyWordSpecLike & Matchers & MockitoExtendedSugar & DefaultAwaitTimeout & OptionValues =>

  def updatingStillConnectedDetails(
    src: String,
    controller: MaxOfLettingsReachedController,
    navigator: AboutFranchisesOrLettingsNavigator
  ) =
    "reply 303 and location header when sourcing from '$src'" in {
      val request = FakeRequest("POST", "/").withFormUrlEncodedBody("maxOfLettings" -> "true")
      val result  = controller.submit(Some("connection"))(request)
      status(result)                   shouldBe SEE_OTHER
      header("Location", result).value shouldBe controllers.connectiontoproperty.routes.ProvideContactDetailsController
        .show()
        .url
      reset(navigator)
    }

  def updatingFranchiseOrLettings(
    src: String,
    controller: MaxOfLettingsReachedController,
    navigator: AboutFranchisesOrLettingsNavigator
  ) =
    s"reply 303 and location header when sourcing from '$src'" in {
      val anyIdentifier             = any[Identifier]
      val anySession                = any[Session]
      val anyHeaderCarrier          = any[HeaderCarrier]
      val anyRequest                = any[Request[AnyContent]]
      val stubSessionToCallFunction = (_: Session) => Call("GET", "url")
      when(navigator.nextPage(anyIdentifier, anySession)(anyHeaderCarrier, anyRequest))
        .thenReturn(stubSessionToCallFunction)
      val request                   = FakeRequest("POST", "/").withFormUrlEncodedBody("maxOfLettings" -> "true")
      val result                    = controller.submit(Some(src))(request)
      status(result)                   shouldBe SEE_OTHER
      header("Location", result).value shouldBe "url"
      // TODO verify(navigator, times(1)).nextPage(MaxOfLettingsReachedCateringId, anySession)(hc, request)
      reset(navigator)
    }

class MaxOfLettingsReachedControllerSpec extends TestBaseSpec with MaxOfLettingsReachedControllerBehaviours {

  val mockAboutFranchisesOrLettingsNavigator = mock[AboutFranchisesOrLettingsNavigator]

  private def maxOfLettingsReachedController = new MaxOfLettingsReachedController(
    stubMessagesControllerComponents(),
    preEnrichedActionRefiner(),
    maxOfLettingsReachedView,
    connectedToPropertyNavigator,
    mockAboutFranchisesOrLettingsNavigator,
    mockSessionRepo
  )

  "GET /" should {
    "return 200 when no scr parameter provided" in {
      val result = maxOfLettingsReachedController.show(None)(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals connection" in {
      val result = maxOfLettingsReachedController.show("connection")(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals franchiseCatering" in {
      val result = maxOfLettingsReachedController.show("franchiseCatering")(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals lettings" in {
      val result = maxOfLettingsReachedController.show("lettings")(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals typeOfIncome" in {
      val result = maxOfLettingsReachedController.show("typeOfIncome")(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals rentalIncome" in {
      val result = maxOfLettingsReachedController.show("rentalIncome")(fakeRequest)
      status(result) shouldBe OK
    }
    "return 200 when  scr parameter equals franchiseLetting" in {
      val result = maxOfLettingsReachedController.show("franchiseLetting")(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = maxOfLettingsReachedController.show(None)(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "return the correct back link for each src parameter" in {
      val testCases = Seq(
        "connection"        -> controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(4).url,
        "franchiseCatering" -> controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController
          .show(4)
          .url,
        "franchiseLetting"  -> controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
          .show(4)
          .url,
        "typeOfIncome"      -> controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show(4).url,
        "rentalIncome"      -> controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(4).url,
        "lettings"          -> controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(9).url,
        ""                  -> routes.TaskListController.show().url
      )

      for ((src, expectedBackLink) <- testCases) {
        val result = maxOfLettingsReachedController.show(Option(src))(fakeRequest)
        status(result) shouldBe OK

        val htmlContent = contentAsString(result)
        htmlContent should include(expectedBackLink)
      }
    }
  }

  "SUBMIT /" should {

    behave like updatingStillConnectedDetails(
      src = "connection",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )
    behave like updatingFranchiseOrLettings(
      src = "franchiseCatering",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )
    behave like updatingFranchiseOrLettings(
      src = "franchiseLetting",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )
    behave like updatingFranchiseOrLettings(
      src = "lettings",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )

    behave like updatingFranchiseOrLettings(
      src = "typeOfIncome",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )

    behave like updatingFranchiseOrLettings(
      src = "rentalIncome",
      maxOfLettingsReachedController,
      mockAboutFranchisesOrLettingsNavigator
    )

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = maxOfLettingsReachedController.submit(None)(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
