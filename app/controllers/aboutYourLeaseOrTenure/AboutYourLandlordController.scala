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

import actions.WithSessionRefiner
import connectors.Audit
import connectors.addressLookup.*
import controllers.{AddressLookupSupport, FORDataCaptureController}
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.theForm
import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.*
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord as AboutYourLandlordView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYourLandlordController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  theView: AboutYourLandlordView,
  addressLookupConnector: AddressLookupConnector,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with AddressLookupSupport(addressLookupConnector)
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AboutYourLandlord")
    val freshForm  = theForm
    val filledForm =
      for
        aboutLeaseOrAgreementPartOne <- request.sessionData.aboutLeaseOrAgreementPartOne
        aboutTheLandlord             <- aboutLeaseOrAgreementPartOne.aboutTheLandlord
      yield theForm.fill(aboutTheLandlord.landlordFullName)

    successful(
      Ok(
        theView(
          filledForm.getOrElse(freshForm),
          request.sessionData.toSummary,
          getBackLink(request.sessionData)
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[String](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(formWithErrors, request.sessionData.toSummary, getBackLink(request.sessionData))
        ),
      formData => {
        given Session = request.sessionData
        for
          newSession     <- successful(sessionWithLandlordFullName(formData))
          _              <- repository.saveOrUpdate(newSession)
          redirectResult <- redirectToAddressLookupFrontend(
                              config = AddressLookupConfig(
                                lookupPageHeadingKey = "aboutYourLandlord.address.lookupPageHeading",
                                selectPageHeadingKey = "aboutYourLandlord.address.selectPageHeading",
                                confirmPageLabelKey = "aboutYourLandlord.address.confirmPageHeading",
                                offRampCall = routes.AboutYourLandlordController.addressLookupCallback("")
                              )
                            )
        yield redirectResult
      }
    )
  }

  private def sessionWithLandlordFullName(landlordFullName: String)(using session: Session) =
    session.copy(
      aboutLeaseOrAgreementPartOne = Some(
        session.aboutLeaseOrAgreementPartOne.fold(
          AboutLeaseOrAgreementPartOne(
            aboutTheLandlord = Some(
              AboutTheLandlord(landlordFullName, landlordAddress = None)
            )
          )
        ) { about =>
          about.copy(
            aboutTheLandlord = Some(
              about.aboutTheLandlord.fold(
                AboutTheLandlord(landlordFullName, landlordAddress = None)
              ) { landlord =>
                landlord.copy(landlordFullName = landlordFullName)
              }
            )
          )
        }
      )
    )

  def addressLookupCallback(id: String) = (Action andThen withSessionRefiner).async { implicit request =>
    given Session = request.sessionData
    for
      confirmedAddress <- getConfirmedAddress(id)
      landlordAddress   = confirmedAddress.asLandlordAddress
      newSession       <- successful(sessionWithLandlordAddress(landlordAddress))
      _                <- repository.saveOrUpdate(newSession)
    yield navigator.from match {
      case "CYA" =>
        Redirect(
          controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
        )
      case _     => Redirect(controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show())
    }
  }

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    if (answers.forType == FOR6020)
      controllers.aboutYourLeaseOrTenure.routes.TypeOfTenureController.show().url
    else
      navigator.from match {
        case "TL" => controllers.routes.TaskListController.show().url + "#about-your-landlord"
        case _    => controllers.routes.TaskListController.show().url
      }

  private def sessionWithLandlordAddress(address: LandlordAddress)(using session: Session) =
    assert(session.aboutLeaseOrAgreementPartOne.isDefined)
    assert(session.aboutLeaseOrAgreementPartOne.get.aboutTheLandlord.isDefined)
    session.copy(
      aboutLeaseOrAgreementPartOne = session.aboutLeaseOrAgreementPartOne.map { about =>
        about.copy(
          aboutTheLandlord = about.aboutTheLandlord.map { landlord =>
            landlord.copy(
              landlordAddress = Some(address)
            )
          }
        )
      }
    )

  extension (confirmed: AddressLookupConfirmedAddress)
    private def asLandlordAddress = LandlordAddress(
      confirmed.buildingNameNumber,
      confirmed.street1,
      confirmed.town,
      confirmed.county,
      confirmed.postcode
    )
