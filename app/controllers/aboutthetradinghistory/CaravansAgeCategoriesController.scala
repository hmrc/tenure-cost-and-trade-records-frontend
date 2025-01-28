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
import form.aboutthetradinghistory.CaravansAgeCategoriesForm.caravansAgeCategoriesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateCaravans
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType.*
import models.submissions.aboutthetradinghistory.{Caravans, CaravansAge}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.Identifier
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansAgeCategories

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

/**
  * 6045/6046 Trading history - How old are the single static caravans on site.
  *
  * @author Yuriy Tumakha
  */
@Singleton
abstract class CaravansAgeCategoriesController @Inject() (
  pageId: Identifier,
  caravanUnitType: CaravanUnitType,
  mcc: MessagesControllerComponents,
  audit: Audit
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  implicit val ec: ExecutionContext = mcc.executionContext

  private val form = caravansAgeCategoriesForm(caravanUnitType)

  val caravansAgeCategoriesView: caravansAgeCategories
  val navigator: AboutTheTradingHistoryNavigator
  val withSessionRefiner: WithSessionRefiner
  val session: SessionRepo

  def savedAnswer(implicit request: SessionRequest[AnyContent]): Option[CaravansAge]

  def updateAnswer(caravansAge: CaravansAge): Caravans => Caravans

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink(pageId.toString)

    Ok(
      caravansAgeCategoriesView(
        savedAnswer.fold(form)(form.fill),
        caravanUnitType,
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CaravansAge](
      form,
      formWithErrors => BadRequest(caravansAgeCategoriesView(formWithErrors, caravanUnitType, getBackLink)),
      data => {
        val updatedData = updateCaravans(updateAnswer(data))

        session
          .saveOrUpdate(updatedData)
          .map(_ => navigator.nextPage(pageId, updatedData).apply(updatedData))
          .map(Redirect)
      }
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.cyaPage
      .filter(_ => navigator.from == "CYA")
      .getOrElse(
        caravanUnitType match {
          case Single => routes.SingleCaravansSubletController.show()
          case Twin   => routes.TwinUnitCaravansSubletController.show()
        }
      )
      .url

}
