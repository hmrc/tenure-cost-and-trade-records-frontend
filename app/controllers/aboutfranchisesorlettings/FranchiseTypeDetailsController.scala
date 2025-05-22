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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import connectors.addressLookup.*
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutfranchisesorlettings.FranchiseTypeDetailsForm.theForm
import models.Session
import models.submissions.aboutfranchisesorlettings.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FranchiseTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.franchiseTypeDetails as FranchiseTypeDetailsView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class FranchiseTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: FranchiseTypeDetailsView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport
    with Logging:

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("FranchiseTypeDetails")
    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        rentalIncome              <- aboutFranchisesOrLettings.rentalIncome
        incomeRecord              <- rentalIncome.lift(index)
        businessDetails           <- incomeRecord match {
                                       case r: FranchiseIncomeRecord      => r.businessDetails
                                       case r: Concession6015IncomeRecord => r.businessDetails
                                       case _                             => None
                                     }
      yield theForm.fill(businessDetails)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        index,
        backLink(index)
      )
    )
  }

  def submit(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[BusinessDetails](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theView(
              formWithErrors,
              idx,
              backLink(idx)
            )
          )
        ),
      formData =>
        for
          (newSession, updatedIndex) <- successful(sessionWithBusinessDetails(formData, idx))
          _                          <- repository.saveOrUpdate(newSession)
          redirectResult             <- redirectToAddressLookupFrontend(
                                          config = AddressLookupConfig(
                                            lookupPageHeadingKey = "concessionDetails.address.lookupPageHeading",
                                            selectPageHeadingKey = "concessionDetails.address.selectPageHeading",
                                            confirmPageLabelKey = "concessionDetails.address.confirmPageHeading",
                                            offRampCall = routes.FranchiseTypeDetailsController.addressLookupCallback(
                                              updatedIndex
                                            )
                                          )
                                        )
        yield redirectResult
    )
  }

  private def sessionWithBusinessDetails(formData: BusinessDetails, idx: Int)(using
    request: SessionRequest[AnyContent]
  ): (Session, Int) = {
    var updatedIndex   = 0
    val updatedSession = updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
      if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
        val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
          records.updated(
            idx,
            records(idx) match {
              // See the @concessionOrfranchise function of the typeOfIncome.scala.html file
              case r: FranchiseIncomeRecord      => r.copy(businessDetails = Some(formData))
              case r: Concession6015IncomeRecord => r.copy(businessDetails = Some(formData))
              // case r: ConcessionIncomeRecord  => r.copy(businessDetails = Some(formData))
              case _                             => throw new IllegalStateException("Unknown income record type")
            }
          )
        }
        updatedIndex = idx
        aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome, rentalIncomeIndex = idx)
      } else {
        aboutFranchisesOrLettings
      }
    }(using request)
    (updatedSession, updatedIndex)
  }

  def addressLookupCallback(idx: Int, id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      businessAddress   = confirmedAddress.asBusinessAddress
      newSession       <- successful(newSessionWithBusinessAddress(idx, businessAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield Redirect(navigator.nextPage(FranchiseTypeDetailsId, newSession).apply(newSession))
  }

  private def backLink(idx: Int)(using request: SessionRequest[AnyContent]): String =
    if request.getQueryString("from") == Some("CYA")
    then routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    else routes.TypeOfIncomeController.show(Some(idx)).url

  extension (confirmed: AddressLookupConfirmedAddress)
    def asBusinessAddress = BusinessAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )

  private def newSessionWithBusinessAddress(idx: Int, addr: BusinessAddress)(using session: Session) =
    assert(session.aboutFranchisesOrLettings.isDefined)
    assert(session.aboutFranchisesOrLettings.get.rentalIncome.isDefined)
    assert(session.aboutFranchisesOrLettings.get.rentalIncome.get.lift(idx).isDefined)
    def patchOne(record: IncomeRecord): IncomeRecord =
      record match
        case rec: FranchiseIncomeRecord      =>
          assert(rec.businessDetails.isDefined)
          rec.copy(
            businessDetails = rec.businessDetails.map(d =>
              d.copy(
                cateringAddress = Some(addr)
              )
            )
          )
        case rec: Concession6015IncomeRecord =>
          assert(rec.businessDetails.isDefined)
          rec.copy(
            businessDetails = rec.businessDetails.map(b =>
              b.copy(
                cateringAddress = Some(addr)
              )
            )
          )
        case _                               => record

    session.copy(
      aboutFranchisesOrLettings = session.aboutFranchisesOrLettings.map { a =>
        a.copy(
          rentalIncome = a.rentalIncome.map { records =>
            records.patch(idx, Seq(patchOne(records(idx))), 1)
          }
        )
      }
    )
