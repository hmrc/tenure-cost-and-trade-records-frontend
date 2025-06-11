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
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutfranchisesorlettings.LettingOtherPartOfPropertyForm.theForm
import models.Session
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{IncomeRecord, LettingAddress, LettingIncomeRecord, OperatorDetails}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.LettingTypeDetailsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.lettingTypeDetails as LettingTypeDetailsView
import models.ForType._

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class LettingTypeDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: LettingTypeDetailsView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with AddressLookupSupport(addressLookupConnector)
    with Logging:

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>

    audit.sendChangeLink("LettingTypeDetails")

    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        rentalIncome              <- aboutFranchisesOrLettings.rentalIncome
        incomeRecord              <- rentalIncome.lift(index)
        operatorDetails           <- incomeRecord match {
                                       case letting: LettingIncomeRecord => letting.operatorDetails
                                       case _                            => None
                                     }
      yield theForm.fill(operatorDetails)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        index,
        backLink(index)
      )
    )
  }

  def submit(idx: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val forType = request.sessionData.forType

    continueOrSaveAsDraft[OperatorDetails](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            idx,
            backLink(idx)
          )
        ),
      formData =>
        for
          (newSession, updatedIndex) <- successful(sessionWithOperatorDetails(formData, idx))
          _                          <- repository.saveOrUpdate(newSession)
          redirectResult             <- redirectToAddressLookupFrontend(
                                          config = AddressLookupConfig(
                                            lookupPageHeadingKey = forType match {
                                              case FOR6010 => "lettingDetails.address.lookupPageHeadingTenant"
                                              case _       => "lettingDetails.address.lookupPageHeading"
                                            },
                                            selectPageHeadingKey = forType match {
                                              case FOR6010 => "lettingDetails.address.selectPageHeadingTenant"
                                              case _       => "lettingDetails.address.selectPageHeading"
                                            },
                                            confirmPageLabelKey = forType match {
                                              case FOR6010 => "lettingDetails.address.confirmPageHeadingTenant"
                                              case _       => "lettingDetails.address.confirmPageHeading"
                                            },
                                            offRampCall = routes.LettingTypeDetailsController.addressLookupCallback(
                                              updatedIndex
                                            )
                                          )
                                        )
        yield redirectResult
    )
  }

  private def sessionWithOperatorDetails(formData: OperatorDetails, idx: Int)(using
    request: SessionRequest[AnyContent]
  ): (Session, Int) =
    var updatedIndex   = 0
    val updatedSession = updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
      if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
        val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
          records.updated(
            idx,
            records(idx) match {
              case r: LettingIncomeRecord => r.copy(operatorDetails = Some(formData))
              case _                      => throw new IllegalStateException("Unknown income record type")
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

  def addressLookupCallback(idx: Int, id: String): Action[AnyContent] = (Action andThen withSessionRefiner).async {
    implicit request =>
      given Session = request.sessionData
      for
        confirmedAddress <- getConfirmedAddress(id)
        businessAddress   = confirmedAddress.asLettingAddress
        newSession       <- successful(newSessionWithLettingAddress(idx, businessAddress))
        _                <- repository.saveOrUpdate(newSession)
      yield Redirect(navigator.nextPage(LettingTypeDetailsId, newSession).apply(newSession))
  }

  private def backLink(idx: Int)(implicit request: SessionRequest[AnyContent]): String =
    request.getQueryString("from") match {
      case Some("CYA") =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
      case _           => controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show(Some(idx)).url
    }

  extension (confirmed: AddressLookupConfirmedAddress)
    def asLettingAddress: LettingAddress = LettingAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )

  private def newSessionWithLettingAddress(idx: Int, addr: LettingAddress)(using session: Session) =
    assert(session.aboutFranchisesOrLettings.isDefined)
    assert(session.aboutFranchisesOrLettings.get.rentalIncome.isDefined)
    assert(session.aboutFranchisesOrLettings.get.rentalIncome.get.lift(idx).isDefined)

    def patchOne(record: IncomeRecord): IncomeRecord =
      record match
        case r: LettingIncomeRecord =>
          assert(r.operatorDetails.isDefined)
          r.copy(
            operatorDetails = r.operatorDetails.map(d =>
              d.copy(
                lettingAddress = Some(addr)
              )
            )
          )
        case _                      => record

    session.copy(
      aboutFranchisesOrLettings = session.aboutFranchisesOrLettings.map { a =>
        a.copy(
          rentalIncome = a.rentalIncome.map { records =>
            records.patch(idx, Seq(patchOne(records(idx))), 1)
          }
        )
      }
    )
