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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.CaravansAnnualPitchFeeForm.caravansAnnualPitchFeeForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateCaravans
import models.submissions.aboutthetradinghistory.{Caravans, CaravansAnnualPitchFee}
import models.submissions.aboutthetradinghistory.Caravans.CaravansPitchFeeServices.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CaravansAnnualPitchFeeId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansAnnualPitchFee

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * 6045/6046 Trading history.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class CaravansAnnualPitchFeeController @Inject() (
  caravansAnnualPitchFeeView: caravansAnnualPitchFee,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  implicit val ec: ExecutionContext = mcc.executionContext

  def savedAnswer(implicit request: SessionRequest[AnyContent]): Option[CaravansAnnualPitchFee] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.caravans)
      .flatMap(_.annualPitchFee)

  def updateAnswer(data: CaravansAnnualPitchFee): Caravans => Caravans =
    _.copy(
      annualPitchFee = Some(
        data.copy(
          rates = data.rates.filter(_ => data.servicesIncludedInPitchFee.contains(Rates)),
          waterAndDrainage =
            data.waterAndDrainage.filter(_ => data.servicesIncludedInPitchFee.contains(WaterAndDrainage)),
          gas = data.gas.filter(_ => data.servicesIncludedInPitchFee.contains(Gas)),
          electricity = data.electricity.filter(_ => data.servicesIncludedInPitchFee.contains(Electricity)),
          otherPitchFeeDetails = data.otherPitchFeeDetails.filter(_ => data.servicesIncludedInPitchFee.contains(Other))
        )
      )
    )

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("CaravansAnnualPitchFee")

    Ok(
      caravansAnnualPitchFeeView(
        savedAnswer.fold(caravansAnnualPitchFeeForm)(caravansAnnualPitchFeeForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CaravansAnnualPitchFee](
      caravansAnnualPitchFeeForm,
      formWithErrors => BadRequest(caravansAnnualPitchFeeView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateCaravans(updateAnswer(data))

        session
          .saveOrUpdate(updatedData)
          .map(_ => navigator.nextPage(CaravansAnnualPitchFeeId, updatedData).apply(updatedData))
          .map(Redirect)
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.cyaPage
      .filter(_ => navigator.from == "CYA")
      .getOrElse(routes.CaravansPerServiceController.show())
      .url

}
