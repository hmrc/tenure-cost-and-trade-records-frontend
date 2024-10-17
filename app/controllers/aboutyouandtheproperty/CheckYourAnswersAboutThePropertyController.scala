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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.CheckYourAnswersAboutThePropertyForm.checkYourAnswersAboutThePropertyForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.CheckYourAnswersAboutYourProperty
import models.ForType.*
import models.Session
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CheckYourAnswersAboutThePropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  checkYourAnswersAboutThePropertyView: checkYourAnswersAboutTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        checkYourAnswersAboutThePropertyView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.checkYourAnswersAboutTheProperty) match {
            case Some(checkYourAnswersAboutTheProperty) =>
              checkYourAnswersAboutThePropertyForm.fill(checkYourAnswersAboutTheProperty)
            case _                                      => checkYourAnswersAboutThePropertyForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CheckYourAnswersAboutYourProperty](
      checkYourAnswersAboutThePropertyForm,
      formWithErrors =>
        BadRequest(
          checkYourAnswersAboutThePropertyView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(checkYourAnswersAboutTheProperty = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url)
          )
        session
          .saveOrUpdate(updatedData)
          .flatMap(_ =>
            Future.successful(
              Redirect(navigator.nextPage(CheckYourAnswersAboutThePropertyPageId, updatedData).apply(updatedData))
            )
          )
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.forType match {
      case FOR6010 | FOR6011 =>
        answers.aboutYouAndTheProperty.flatMap(_.tiedForGoods.map(_.name)) match {
          case Some("yes") => controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show().url
          case Some("no")  => controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
          case _           =>
            logger.warn(s"Back link for enforcement action page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case FOR6015 | FOR6016 =>
        answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseGrantedDetail.map(_.name)) match {
          case Some("yes") =>
            controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show().url
          case Some("no")  => controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
          case _           =>
            logger.warn(s"Back link for premises license page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
      case FOR6030           =>
        answers.aboutYouAndTheProperty.flatMap(_.charityQuestion.map(_.name)) match {
          case Some("yes") => controllers.aboutyouandtheproperty.routes.TradingActivityController.show().url
          case Some("no")  => controllers.aboutyouandtheproperty.routes.CharityQuestionController.show().url
          case _           =>
            logger.warn(s"Backlink error, No value for charity question")
            controllers.routes.TaskListController.show().url
        }
      case FOR6020           => controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      case FOR6045 | FOR6046 =>
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
      case FOR6076           => controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show().url
      case _                 => controllers.routes.LoginController.show.url
    }
}
