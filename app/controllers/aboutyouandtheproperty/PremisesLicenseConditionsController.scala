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
import form.aboutyouandtheproperty.PremisesLicenseConditionsForm.premisesLicenseConditionsForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.common.AnswersYesNo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.PremisesLicenceConditionsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.premisesLicenseConditions

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PremisesLicenseConditionsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  premisesLicenseView: premisesLicenseConditions,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PremisesLicenseConditions")
    Future.successful(
      Ok(
        premisesLicenseView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.premisesLicenseConditions) match {
            case Some(premisesLicense) => premisesLicenseConditionsForm.fill(premisesLicense)
            case _                     => premisesLicenseConditionsForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      premisesLicenseConditionsForm,
      formWithErrors =>
        BadRequest(
          premisesLicenseView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(premisesLicenseConditions = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PremisesLicenceConditionsPageId, updatedData).apply(updatedData)))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.aboutYouAndTheProperty.flatMap(_.licensableActivities.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show().url
      case Some("no")  => controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show().url
      case _           =>
        logger.warn(s"Back link for premises licence conditions page reached with unknown licence activity value")
        controllers.routes.TaskListController.show().url
    }
}
