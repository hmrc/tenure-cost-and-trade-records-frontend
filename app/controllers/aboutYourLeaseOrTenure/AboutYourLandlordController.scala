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

package controllers.aboutYourLeaseOrTenure

import actions.{SessionRequest, WithSessionRefiner}
import connectors.AddressLookupConnector
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.AboutTheLandlord
import util.AddressLookupUtil

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYourLandlordController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  aboutYourLandlordView: aboutYourLandlord,
  addressLookupConnector: AddressLookupConnector,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutYourLandlordView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.aboutTheLandlord) match {
            case Some(aboutTheLandlord) => aboutTheLandlordForm.fill(aboutTheLandlord)
            case _                      => aboutTheLandlordForm
          },
          request.sessionData.toSummary,
          navigator.from
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    saveAndForwardToAddressLookup()
  }

  def addressLookupCallback(id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    val mayBeAddressLookup = addressLookupConnector.getAddress(id)

    mayBeAddressLookup.flatMap { addressLookup =>
      val landlordAddress = AddressLookupUtil.getLandLordAddress(addressLookup)

      val existingFullName = request.sessionData.aboutLeaseOrAgreementPartOne
        .flatMap(_.aboutTheLandlord.map(_.landlordFullName))
        .getOrElse("")

      val updatedAboutTheLandlord = AboutTheLandlord(
        landlordFullName = existingFullName,
        landlordAddress = Some(landlordAddress)
      )

      val updatedSessionData = request.sessionData.copy(
        aboutLeaseOrAgreementPartOne = request.sessionData.aboutLeaseOrAgreementPartOne.map(
          _.copy(aboutTheLandlord = Some(updatedAboutTheLandlord))
        )
      )
      session.saveOrUpdate(updatedSessionData).map { _ =>
        navigator.from match {
          case "CYA" =>
            Redirect(controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show())
          case "TL" => Redirect(controllers.routes.TaskListController.show())
          case _     => Redirect(controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show())
        }
      }
    }
  }

  private def saveAndForwardToAddressLookup()(implicit request: SessionRequest[AnyContent]) =
    continueOrSaveAsDraft[AboutTheLandlord](
      aboutTheLandlordForm,
      formWithErrors => BadRequest(aboutYourLandlordView(formWithErrors, request.sessionData.toSummary)),
      data => {

        implicit val language: Lang = mcc.messagesApi.preferred(request).lang
        val from                    = navigator.from
        val updatedData             = updateAboutLeaseOrAgreementPartOne(_.copy(aboutTheLandlord = Some(data)))
        session.saveOrUpdate(updatedData)
        addressLookupConnector
          .initialise(routes.AboutYourLandlordController.addressLookupCallback(), from)
          .flatMap {
            case Some(url) =>
              Future.successful(SeeOther(url))
            case None      => Future.successful(InternalServerError("Address lookup initialization failed"))
          }
      }
    )
}
