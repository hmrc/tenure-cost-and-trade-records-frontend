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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.LeaseOrAgreementYearsForm.leaseOrAgreementYearsForm
import models.Session
import models.audit.ChangeLinkAudit
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.LeaseOrAgreementYearsDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.LeaseOrAgreementDetailsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.leaseOrAgreementYears

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LeaseOrAgreementYearsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  leaseOrAgreementYearsView: leaseOrAgreementYears,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("LeaseOrAgreementYears")

    Future.successful(
      Ok(
        leaseOrAgreementYearsView(
          leaseOrAgreementDetailsInSession match {
            case Some(leaseOrAgreementYearsDetails) => leaseOrAgreementYearsForm.fill(leaseOrAgreementYearsDetails)
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
        val sessionContains3No = leaseOrAgreementDetailsInSession.exists(contains3No)
        val updatedData        = updateAboutLeaseOrAgreementPartOne(_.copy(leaseOrAgreementYearsDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ => navigator.from == "CYA" && contains3No(data) == sessionContains3No)
              .getOrElse(
                navigator.nextWithoutRedirectToCYA(LeaseOrAgreementDetailsPageId, updatedData).apply(updatedData)
              )
          }
          .map(Redirect)
      }
    )
  }

  private def contains3No(d: LeaseOrAgreementYearsDetails): Boolean =
    Seq(d.commenceWithinThreeYears, d.agreedReviewedAlteredThreeYears, d.rentUnderReviewNegotiated)
      .forall(_.name == "no")

  private def leaseOrAgreementDetailsInSession(implicit
    request: SessionRequest[AnyContent]
  ): Option[LeaseOrAgreementYearsDetails] =
    request.sessionData.aboutLeaseOrAgreementPartOne
      .flatMap(_.leaseOrAgreementYearsDetails)

  private def getBackLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#lease-or-agreement-details"
      case _    =>
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

}
