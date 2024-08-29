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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.{FORDataCaptureController, aboutYourLeaseOrTenure}
import form.aboutYourLeaseOrTenure.IsRentUnderReviewForm.isRentUnderReviewForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IsRentUnderReviewId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.isRentUnderReview

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IsRentUnderReviewController @Inject() (
  isRentUnderReviewView: isRentUnderReview,
  navigator: AboutYourLeaseOrTenureNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      isRentUnderReviewView(
        leaseOrAgreementPartThree
          .flatMap(_.isRentUnderReview)
          .fold(isRentUnderReviewForm)(isRentUnderReviewForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      isRentUnderReviewForm,
      formWithErrors => BadRequest(isRentUnderReviewView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(isRentUnderReview = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(IsRentUnderReviewId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartThree(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartThree] = request.sessionData.aboutLeaseOrAgreementPartThree

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    request.sessionData.forType match {
      case ForTypes.for6045 | ForTypes.for6046 => routes.MethodToFixCurrentRentController.show().url
      case _ => routes.IntervalsOfRentReviewController.show().url
    }

}
