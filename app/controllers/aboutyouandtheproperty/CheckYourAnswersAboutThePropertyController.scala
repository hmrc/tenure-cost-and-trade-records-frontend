/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutyouandtheproperty.CheckYourAnswersAboutThePropertyForm.theForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.ForType.*
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.CheckYourAnswersAboutThePropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.checkYourAnswersAboutTheProperty as CheckYourAnswersAboutThePropertyView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersAboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  theView: CheckYourAnswersAboutThePropertyView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val freshForm = theForm
    val filledForm =
      for
        aboutYouAndTheProperty <- request.sessionData.aboutYouAndTheProperty
        answersYesNo <- aboutYouAndTheProperty.checkYourAnswersAboutTheProperty
      yield theForm.fill(answersYesNo)

    Ok(
        theView(
          filledForm.getOrElse(freshForm),
          backLinkUrl(request.sessionData),
          request.sessionData.toSummary
        )
      )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            backLinkUrl(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(checkYourAnswersAboutTheProperty = Some(data)))
          .copy(lastCYAPageUrl =
            Some(controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url)
          )
        repo
          .saveOrUpdate(updatedData)
          .flatMap(_ =>
            Future.successful(
              Redirect(navigator.nextPage(CheckYourAnswersAboutThePropertyPageId, updatedData).apply(updatedData))
            )
          )
      }
    )
  }

  private def backLinkUrl(answers: Session): String =
    answers.forType match {
      case FOR6010 | FOR6011 =>
        answers.aboutYouAndTheProperty.flatMap(_.tiedForGoods) match {
          case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show().url
          case _               => controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show().url
        }
      case FOR6015 | FOR6016 =>
        answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseGrantedDetail) match {
          case Some(AnswerYes) =>
            controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show().url
          case _               => controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url
        }
      case FOR6030           =>
        answers.aboutYouAndTheProperty.flatMap(_.charityQuestion) match {
          case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.TradingActivityController.show().url
          case _               => controllers.aboutyouandtheproperty.routes.CharityQuestionController.show().url
        }
      case FOR6020           => controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      case FOR6045 | FOR6046 =>
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show().url
      case FOR6048           =>
        answers.aboutYouAndThePropertyPartTwo.flatMap(_.partsUnavailable) match
          case Some(AnswerYes) =>
            controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController
              .show(answers.aboutYouAndThePropertyPartTwo.fold(0)(_.occupiersListIndex))
              .url
          case _               =>
            controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show().url
      case FOR6076           => controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show().url
    }
