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
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConnector}
import controllers.{AddressLookupSupport, FORDataCaptureController}
import models.submissions.aboutfranchisesorlettings.{ATMLetting, AboutFranchisesOrLettings, LettingPartOfProperty}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.atmLetting
import form.aboutfranchisesorlettings.ATMLettingForm.theForm
import models.Session
import models.submissions.common.Address

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

class AtmLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  atmLettingView: atmLetting,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport
    with Logging:

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AtmLetting")
    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        lettings                  <- aboutFranchisesOrLettings.lettings
        requestedIndex            <- index
        requestedLetting          <- lettings.lift(requestedIndex)
        letting                   <- requestedLetting match {
                                       case letting: ATMLetting => Some(letting)
                                       case _                   => None
                                     }
      yield theForm.fill(letting)

    Ok(
      atmLettingView(
        filledForm.getOrElse(freshForm),
        index,
        backLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ATMLetting](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            atmLettingView(
              formWithErrors,
              index,
              backLink(index),
              request.sessionData.toSummary
            )
          )
        ),
      formData => {
        var updatedIndex: Int = -1
        val updatedSession    = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings { about =>
          val (updatedLettings, idx) = updateOrAddATMLetting(about.lettings, formData, index)
          updatedIndex = idx
          about.copy(
            lettingCurrentIndex = updatedIndex,
            lettings = Some(updatedLettings)
          )
        }(using request)

        for
          _              <- repository.saveOrUpdate(updatedSession)
          redirectResult <- redirectToAddressLookupFrontend(
                              config = AddressLookupConfig(
                                lookupPageHeadingKey = "atmLetting.address.lookupPageHeading",
                                selectPageHeadingKey = "atmLetting.address.selectPageHeading",
                                confirmPageLabelKey = "atmLetting.address.confirmPageHeading",
                                offRampCall = routes.AtmLettingController.addressLookupCallback(updatedIndex)
                              )
                            )
        yield redirectResult
      }
    )
  }

  private def updateOrAddATMLetting(
    lettingsOpt: Option[IndexedSeq[LettingPartOfProperty]],
    atmLetting: ATMLetting,
    index: Option[Int]
  ): (IndexedSeq[LettingPartOfProperty], Int) = {
    var updatedIndex: Int = -1
    val updatedLetting    = lettingsOpt match {
      case Some(lettings) =>
        index match {
          case Some(idx) if idx < lettings.length =>
            updatedIndex = idx
            lettings(idx) match {
              case existingATM: ATMLetting =>
                lettings.updated(
                  idx,
                  existingATM.copy(
                    bankOrCompany = atmLetting.bankOrCompany,
                    correspondenceAddress = atmLetting.correspondenceAddress
                  )
                )
              case _                       =>
                lettings.updated(idx, atmLetting)
            }
          case _                                  =>
            updatedIndex = lettings.length
            lettings :+ atmLetting
        }
      case None           =>
        updatedIndex = 0
        IndexedSeq(atmLetting)
    }
    (updatedLetting, updatedIndex)
  }

  def addressLookupCallback(idx: Int, id: String): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      lettingAddress   <- confirmedAddress.asAddress
      newSession       <- successful(newSessionWithLettingAddress(idx, lettingAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield
      if navigator.from == "CYA"
      then Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
      else Redirect(routes.RentDetailsController.show(idx))
  }

  private def backLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if navigator.from == "CYA"
    then routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    else routes.TypeOfLettingController.show(idx).url

  private def newSessionWithLettingAddress(idx: Int, lettingAddress: Address)(using session: Session) =
    assert(session.aboutFranchisesOrLettings.isDefined)
    assert(session.aboutFranchisesOrLettings.get.lettings.isDefined)
    session.copy(
      aboutFranchisesOrLettings = session.aboutFranchisesOrLettings.map { a =>
        a.copy(
          lettings = a.lettings.map { ls =>
            ls.lift(idx)
              .map {
                case l: ATMLetting =>
                  ls.updated(
                    idx,
                    l.copy(
                      correspondenceAddress = Some(lettingAddress)
                    )
                  )
                case _             => ls
              }
              .getOrElse(ls)
          }
        )
      }
    )
