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
import form.aboutthetradinghistory.CaravansAgeCategoriesForm.caravansAgeCategoriesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateCaravans
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType.Single
import models.submissions.aboutthetradinghistory.{Caravans, CaravansAge}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.SingleCaravansAgeCategoriesId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.caravansAgeCategories

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * 6045/6046 Trading history - How old are the single static caravans on site.
  *
  * @author Yuriy Tumakha
  */
@Singleton
class SingleCaravansAgeCategoriesController @Inject() (
  caravansAgeCategoriesView: caravansAgeCategories,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  private val pageId          = SingleCaravansAgeCategoriesId
  private val caravanUnitType = Single
  private val form            = caravansAgeCategoriesForm(caravanUnitType)

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
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

  private def savedAnswer(implicit
    request: SessionRequest[AnyContent]
  ): Option[CaravansAge] = request.sessionData.aboutTheTradingHistoryPartOne
    .flatMap(_.caravans)
    .flatMap(_.singleCaravansAge)

  private def updateAnswer(caravansAge: CaravansAge): Caravans => Caravans =
    _.copy(singleCaravansAge = Some(caravansAge))

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.cyaPage
      .filter(_ => navigator.from == "CYA")
      .getOrElse( // TODO: Single caravans sub-let by operator to holidaymakers on behalf of private owners as fleet hire
        routes.GrossReceiptsCaravanFleetHireController.show()
      )
      .url

}
