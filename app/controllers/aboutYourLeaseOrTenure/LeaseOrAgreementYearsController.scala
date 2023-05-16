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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.LeaseOrAgreementYearsForm.leaseOrAgreementYearsForm
import models.Session
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.LeaseOrAgreementYearsDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LeaseOrAgreementDetailsPageId
import play.api.i18n.I18nSupport
import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.leaseOrAgreementYears

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LeaseOrAgreementYearsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYourLeaseOrTenureNavigator,
  leaseOrAgreementYearsView: leaseOrAgreementYears,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        leaseOrAgreementYearsView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.leaseOrAgreementYearsDetails) match {
            case Some(leaseOrAgreementYearsDetails) =>
              leaseOrAgreementYearsForm.fillAndValidate(leaseOrAgreementYearsDetails)
            case _                                  => leaseOrAgreementYearsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LeaseOrAgreementYearsDetails](
      leaseOrAgreementYearsForm,
      formWithErrors =>
        BadRequest(
          leaseOrAgreementYearsView(formWithErrors, getBackLink(request.sessionData), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(leaseOrAgreementYearsDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(LeaseOrAgreementDetailsPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlord.map(_.name)) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
      case Some("no")  =>
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      case _           =>
        logger.warn(s"Back link for lease or agreement page reached with unknown enforcement taken value")
        controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url
    }

}
