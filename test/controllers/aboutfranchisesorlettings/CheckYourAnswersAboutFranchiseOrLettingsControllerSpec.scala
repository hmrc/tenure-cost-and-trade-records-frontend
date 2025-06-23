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

package controllers.aboutfranchisesorlettings

import form.aboutfranchisesorlettings.CheckYourAnswersAboutFranchiseOrLettingsForm.theForm
import models.ForType.*
import models.ForType.FOR6010
import models.submissions.aboutfranchisesorlettings.TypeOfIncome.{TypeConcession, TypeFranchise, TypeLetting}
import models.{ForType, Session}
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, BusinessDetails, CalculatingTheRent, Concession6015IncomeRecord, ConcessionBusinessDetails, ConcessionIncomeRecord, FeeReceived, FeeReceivedPerYear, FranchiseIncomeRecord, LettingIncomeRecord, OperatorDetails, PropertyRentDetails, RentReceivedFrom}
import models.submissions.common.AnswersYesNo.AnswerYes
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.Future.successful
import scala.language.reflectiveCalls

class CheckYourAnswersAboutFranchiseOrLettingsControllerSpec extends TestBaseSpec:

  import TestData.*

  def checkYourAnswersAboutFranchiseOrLettingsController6045(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6045)
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6045),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsController6020(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsWith6020LettingsAll
    )
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
      mockSessionRepo
    )

  def checkYourAnswersAboutFranchiseOrLettingsControllerNo(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(
      prefilledAboutFranchiseOrLettingsNo
    )
  ) =
    new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings, forType = FOR6020),
      mockSessionRepo
    )

  "GET /" should {

    "return HTML FOR6010 about Franchise or Lettings" in new ControllerFixture(forType = FOR6010) {
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML FOR6015 about Concessions or Lettings" in new ControllerFixture(forType = FOR6015) {
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML FOR6020 about Lettings" in new ControllerFixture(forType = FOR6020) {
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML FOR6030 about Franchise or Lettings" in new ControllerFixture(forType = FOR6030) {
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML FOR6045 about Concessions, Franchises or Lettings" in new ControllerFixture(forType = FOR6045) {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML FOR6046 about Concessions, Franchises or Lettings" in new ControllerFixture(forType = FOR6046) {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in new ControllerFixture(forType = FOR6010) {
      val result = controller.show()(FakeRequest(GET, "/path?from=CYA"))
      contentAsString(result) should include(
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in new ControllerFixture(forType = FOR6010) {
      val res = controller.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "SUBMIT /"                               should {
    "throw a BAD_REQUEST if an empty form is submitted for 6020" in {
      val res = checkYourAnswersAboutFranchiseOrLettingsController6020().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
  "Add another letting accommodation form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersAboutFranchiseOrLettings
      val form     = theForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersAboutFranchiseOrLettings, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val checkYourAnswersAboutFranchiseOrLettings: String =
        "checkYourAnswersAboutFranchiseOrLettings"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersAboutFranchiseOrLettings" -> "yes")
  }

  trait ControllerFixture(forType: ForType) extends TestBaseSpec:
    val repository = mock[SessionRepo]
    when(repository.saveOrUpdate(any[Session])(using any[HeaderCarrier])).thenReturn(successful(()))

    val controller = new CheckYourAnswersAboutFranchiseOrLettingsController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      checkYourAnswersAboutFranchiseOrLettings,
      preEnrichedActionRefiner(
        aboutFranchisesOrLettings = Some(
          AboutFranchisesOrLettings(
            franchisesOrLettingsTiedToProperty = Some(AnswerYes),
            currentMaxOfLetting = None,
            checkYourAnswersAboutFranchiseOrLettings = None,
            fromCYA = None,
            lettings = None,
            rentalIncome = forType match {
              case FOR6010 =>
                Some(
                  IndexedSeq( // Franchise or Lettings
                    FranchiseIncomeRecord(
                      sourceType = TypeFranchise,
                      businessDetails = Some(
                        BusinessDetails(
                          operatorName = "Mr. Pizza",
                          typeOfBusiness = "Restaurant",
                          cateringAddress = None
                        )
                      )
                    ),
                    LettingIncomeRecord(
                      sourceType = TypeLetting,
                      operatorDetails = Some(
                        OperatorDetails(
                          operatorName = "Mr. Lettings",
                          typeOfBusiness = "Letting Agency",
                          lettingAddress = None
                        )
                      ),
                      rent = Some(
                        PropertyRentDetails(
                          annualRent = 1200L,
                          dateInput = LocalDate.of(2023, 4, 1)
                        )
                      ),
                      itemsIncluded = Some(List("Furnishings", "Utilities"))
                    )
                  )
                )
              case FOR6015 =>
                Some(
                  IndexedSeq( // Concession or Lettings
                    Concession6015IncomeRecord(
                      sourceType = TypeFranchise,
                      businessDetails = Some(
                        BusinessDetails(
                          operatorName = "Mr. Pizza",
                          typeOfBusiness = "Restaurant",
                          cateringAddress = None
                        )
                      ),
                      rent = Some(
                        RentReceivedFrom(
                          annualRent = 1200L,
                          declaration = true
                        )
                      ),
                      calculatingTheRent = Some(
                        CalculatingTheRent(
                          description = "Rent calculated based on annual income",
                          dateInput = LocalDate.of(2023, 4, 1)
                        )
                      ),
                      itemsIncluded = Some(List("Furnishings", "Utilities"))
                    )
                  )
                )
              case FOR6045 =>
                Some(
                  IndexedSeq( // Concessions, Franchises or Lettings))
                    ConcessionIncomeRecord(
                      sourceType = TypeConcession,
                      businessDetails = Some(
                        ConcessionBusinessDetails(
                          operatorName = "Mr. Concession",
                          typeOfBusiness = "Concession Stand",
                          howBusinessPropertyIsUsed = "Food and Beverages"
                        )
                      ),
                      feeReceived = Some(
                        FeeReceived(
                          feeReceivedPerYear = Seq(
                            FeeReceivedPerYear(
                              financialYearEnd = LocalDate.of(2023, 3, 31),
                              tradingPeriod = 5
                            )
                          ),
                          feeCalculationDetails = Some("Annual fee based on sales")
                        )
                      ),
                      addAnotherRecord = Some(AnswerYes)
                    )
                  )
                )
              case _       => None
            },
            rentalIncomeIndex = 0,
            lettingCurrentIndex = 0,
            rentalIncomeMax = None
          )
        ),
        forType = forType
      ),
      repository
    )
