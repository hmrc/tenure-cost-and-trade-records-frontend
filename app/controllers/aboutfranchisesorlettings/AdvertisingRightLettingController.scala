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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutfranchisesorlettings.AdvertisingRightLettingForm.theForm
import models.Session
import models.submissions.aboutfranchisesorlettings.{AboutFranchisesOrLettings, AdvertisingRightLetting, LettingAddress, LettingPartOfProperty}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.advertisingRightLetting as AdvertisingRightLettingView

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

class AdvertisingRightLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theView: AdvertisingRightLettingView,
  withSessionRefiner: WithSessionRefiner,
  addressLookupConnector: AddressLookupConnector,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport
    with Logging:

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AdvertisingRightLetting")
    val freshForm  = theForm
    val filledForm =
      for
        aboutFranchisesOrLettings <- request.sessionData.aboutFranchisesOrLettings
        lettings                  <- aboutFranchisesOrLettings.lettings
        requestedIndex            <- index
        requestedLetting          <- lettings.lift(requestedIndex)
        letting                   <- requestedLetting match {
                                       case letting: AdvertisingRightLetting => Some(letting)
                                       case _                                => None
                                     }
      yield theForm.fill(letting)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        index,
        backLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AdvertisingRightLetting](
      theForm,
      formWithErrors =>
        successful(
          BadRequest(
            theView(
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
          val (updatedLettings, idx) = updateOrAddAdvertisingRightLetting(about.lettings, formData, index)
          updatedIndex = idx
          about.copy(
            lettingCurrentIndex = updatedIndex,
            lettings = Some(updatedLettings)
          )
        }(request)

        for
          _              <- repository.saveOrUpdate(updatedSession)
          redirectResult <- redirectToAddressLookupFrontend(
                              config = AddressLookupConfig(
                                lookupPageHeadingKey = "advertisingRightLetting.address.lookupPageHeading",
                                selectPageHeadingKey = "advertisingRightLetting.address.selectPageHeading",
                                confirmPageLabelKey = "advertisingRightLetting.address.confirmPageHeading",
                                offRampCall =
                                  routes.AdvertisingRightLettingController.addressLookupCallback(updatedIndex, "")
                              )
                            )
        yield redirectResult
      }
    )
  }

  private def updateOrAddAdvertisingRightLetting(
    lettingsOpt: Option[IndexedSeq[LettingPartOfProperty]],
    advertisingRightLetting: AdvertisingRightLetting,
    index: Option[Int]
  ): (IndexedSeq[LettingPartOfProperty], Int) = {
    var updatedIndex: Int = -1
    val updatedLetting    = lettingsOpt match {
      case Some(lettings) =>
        index match {
          case Some(idx) if idx < lettings.length =>
            updatedIndex = idx
            lettings(idx) match {
              case existingOther: AdvertisingRightLetting =>
                lettings.updated(
                  idx,
                  existingOther.copy(
                    advertisingCompanyName = advertisingRightLetting.advertisingCompanyName,
                    descriptionOfSpace = advertisingRightLetting.descriptionOfSpace,
                    correspondenceAddress = advertisingRightLetting.correspondenceAddress
                  )
                )
              case _                                      =>
                lettings.updated(idx, advertisingRightLetting)
            }
          case _                                  =>
            updatedIndex = lettings.length
            lettings :+ advertisingRightLetting
        }
      case None           =>
        updatedIndex = 0
        IndexedSeq(advertisingRightLetting)
    }
    (updatedLetting, updatedIndex)
  }

  private def backLink(idx: Option[Int])(implicit request: SessionRequest[AnyContent]): String =
    if (navigator.from == "CYA") {
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show().url
    } else {
      controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(idx).url
    }

  def addressLookupCallback(idx: Int, id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      lettingAddress   <- confirmedAddress.asLettingAddress
      newSession       <- successful(newSessionWithLettingAddress(idx, lettingAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield
      if navigator.from == "CYA"
      then Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
      else Redirect(routes.RentDetailsController.show(idx))
  }

  extension (confirmed: AddressLookupConfirmedAddress)
    def asLettingAddress = LettingAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )

  private def newSessionWithLettingAddress(idx: Int, lettingAddress: LettingAddress)(using session: Session) =
    assert(session.aboutFranchisesOrLettings.isDefined)
    assert(session.aboutFranchisesOrLettings.get.lettings.isDefined)
    session.copy(
      aboutFranchisesOrLettings = session.aboutFranchisesOrLettings.map { a =>
        a.copy(
          lettings = a.lettings.map { ls =>
            ls.lift(idx)
              .map {
                case l: AdvertisingRightLetting =>
                  ls.updated(
                    idx,
                    l.copy(
                      correspondenceAddress = Some(lettingAddress)
                    )
                  )
                case _                          => ls
              }
              .getOrElse(ls)
          }
        )
      }
    )
