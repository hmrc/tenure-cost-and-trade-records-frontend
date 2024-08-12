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

package controllers.aboutthetradinghistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.CaravansPerServiceForm.caravansPerServiceForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateCaravans
import models.submissions.aboutthetradinghistory.{Caravans, CaravansPerService}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CaravansPerServiceId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansPerService

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * 6045/6046 Trading history.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class CaravansPerServiceController @Inject() (
  caravansPerServiceView: caravansPerService,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  implicit val ec: ExecutionContext = mcc.executionContext

  def savedAnswer(implicit request: SessionRequest[AnyContent]): Option[CaravansPerService] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.caravans)
      .flatMap(_.caravansPerService)

  def updateAnswer(caravansPerService: CaravansPerService): Caravans => Caravans =
    _.copy(caravansPerService = Some(caravansPerService))

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(
      caravansPerServiceView(
        savedAnswer.fold(caravansPerServiceForm)(caravansPerServiceForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CaravansPerService](
      caravansPerServiceForm,
      formWithErrors => BadRequest(caravansPerServiceView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateCaravans(updateAnswer(data))

        session
          .saveOrUpdate(updatedData)
          .map(_ => navigator.nextPage(CaravansPerServiceId, updatedData).apply(updatedData))
          .map(Redirect)
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.cyaPage
      .filter(_ => navigator.from == "CYA")
      .getOrElse(routes.CaravansTotalSiteCapacityController.show())
      .url

}
