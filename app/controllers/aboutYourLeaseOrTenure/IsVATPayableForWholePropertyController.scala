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
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.IsVATPayableForWholePropertyForm.isVATPayableForWholePropertyForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.common.AnswersYesNo
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.IsVATPayableForWholePropertyId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.isVATPayableForWholeProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class IsVATPayableForWholePropertyController @Inject() (
  isVATPayableForWholePropertyView: isVATPayableForWholeProperty,
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
      isVATPayableForWholePropertyView(
        leaseOrAgreementPartThree
          .flatMap(_.isVATPayableForWholeProperty)
          .fold(isVATPayableForWholePropertyForm)(isVATPayableForWholePropertyForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      isVATPayableForWholePropertyForm,
      formWithErrors => BadRequest(isVATPayableForWholePropertyView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutLeaseOrAgreementPartThree(_.copy(isVATPayableForWholeProperty = Some(data)))

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(IsVATPayableForWholePropertyId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def leaseOrAgreementPartThree(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutLeaseOrAgreementPartThree] = request.sessionData.aboutLeaseOrAgreementPartThree

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url

}
