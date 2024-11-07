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

package controllers

import actions.{SessionRequest, WithSessionRefiner}
import form.MaxOfLettingsForm.maxOfLettingsForm
import models.submissions.MaxOfLettings
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.{AboutFranchisesOrLettingsNavigator, ConnectionToPropertyNavigator}
import navigation.identifiers.{MaxOfLettingsReachedCurrentId, MaxOfLettingsReachedId}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.maxOfLettingsReached

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MaxOfLettingsReachedController @Inject() (
  mcc: MessagesControllerComponents,
  withSessionRefiner: WithSessionRefiner,
  maxOfLettingsReachedView: maxOfLettingsReached,
  connectionNavigator: ConnectionToPropertyNavigator,
  franchiseNavigator: AboutFranchisesOrLettingsNavigator,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(src: Option[String]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val (backLink, form, sourceOpt) = getSourceDetails(src, request)
    val filledForm                  = form.map(maxOfLettingsForm.fill).getOrElse(maxOfLettingsForm)

    Future.successful(
      Ok(
        maxOfLettingsReachedView(
          filledForm,
          backLink,
          sourceOpt,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit(src: Option[String]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val (backLink, _, sourceOpt) = getSourceDetails(src, request)

    continueOrSaveAsDraft[MaxOfLettings](
      maxOfLettingsForm,
      formWithErrors =>
        BadRequest(
          maxOfLettingsReachedView(
            formWithErrors,
            backLink,
            sourceOpt,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = src match {
          case Some("connection")        =>
            updateStillConnectedDetails(_.copy(maxOfLettings = Some(data)))
          case Some("franchiseCatering") =>
            updateAboutFranchisesOrLettings(_.copy(cateringMaxOfLettings = Some(data)))
          case Some("franchiseLetting")  =>
            updateAboutFranchisesOrLettings(_.copy(currentMaxOfLetting = data))
          case Some("lettings")          => updateAboutFranchisesOrLettings(_.copy(currentMaxOfLetting = data))
          case Some("rentalIncome")      => updateAboutFranchisesOrLettings(_.copy(rentalIncomeMax = Some(data)))

        }
        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            src match {
              case Some("connection") =>
                connectionNavigator
                  .cyaPageDependsOnSession(updatedData)
                  .filter(_ => connectionNavigator.from == "CYA")
                  .getOrElse(
                    connectionNavigator
                      .nextWithoutRedirectToCYA(MaxOfLettingsReachedId, updatedData)
                      .apply(updatedData)
                  )
              case _                  => franchiseNavigator.nextPage(MaxOfLettingsReachedCurrentId, updatedData).apply(updatedData)
            }
          }
          .map(Redirect)
      }
    )
  }

  private def getSourceDetails(
    source: Option[String],
    request: SessionRequest[AnyContent]
  ): (String, Option[MaxOfLettings], String) =
    source match {
      case Some("connection")        =>
        (
          controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(4).url,
          request.sessionData.stillConnectedDetails.flatMap(_.maxOfLettings),
          "connection"
        )
      case Some("franchiseCatering") =>
        (
          controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(4).url,
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringMaxOfLettings),
          "franchiseCatering"
        )
      case Some("franchiseLetting")  =>
        (
          controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(4).url,
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.currentMaxOfLetting),
          "franchiseLetting"
        )
      case Some("rentalIncome")      =>
        (
          controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(4).url,
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncomeMax),
          "rentalIncome"
        )
      case Some("lettings")          =>
        (
          controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(9).url,
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.currentMaxOfLetting),
          "lettings"
        )
      case _                         =>
        (
          routes.TaskListController.show().url,
          None,
          ""
        )
    }
}
