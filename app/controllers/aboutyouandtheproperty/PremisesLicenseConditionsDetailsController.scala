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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.PremisesLicenseConditionsDetails
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.PremisesLicenceConditionsDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.premisesLicenseConditionsDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PremisesLicenseConditionsDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  premisesLicenseConditionsView: premisesLicenseConditionsDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PremisesLicenseConditionsDetails")

    Future.successful(
      Ok(
        premisesLicenseConditionsView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.premisesLicenseConditionsDetails) match {
            case Some(premisesLicenseInformation) =>
              premisesLicenceDetailsForm.fill(premisesLicenseInformation)
            case _                                => premisesLicenceDetailsForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PremisesLicenseConditionsDetails](
      premisesLicenceDetailsForm,
      formWithErrors =>
        BadRequest(
          premisesLicenseConditionsView(
            formWithErrors,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(premisesLicenseConditionsDetails = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            Redirect(navigator.nextPage(PremisesLicenceConditionsDetailsPageId, updatedData).apply(updatedData))
          )
      }
    )
  }

}
