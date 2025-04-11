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
import form.aboutthetradinghistory.GrossReceiptsExcludingVATForm.grossReceiptsExcludingVATForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import models.submissions.aboutthetradinghistory.{GrossReceiptsExcludingVAT, TurnoverSection6076}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossReceiptsExcludingVatId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.grossReceiptsExcludingVAT

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GrossReceiptsExcludingVATController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: grossReceiptsExcludingVAT,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("GrossReceiptsExcludingVAT")

    runWithSessionCheck { turnoverSections6076 =>
      val grossReceiptsExcludingVATSeq = turnoverSections6076.flatMap(_.grossReceiptsExcludingVAT)

      Ok(
        view(
          grossReceiptsExcludingVATForm(years(turnoverSections6076)).fill(grossReceiptsExcludingVATSeq),
          navigator.from
        )
      )
    }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { turnoverSections6076 =>
      continueOrSaveAsDraft[Seq[GrossReceiptsExcludingVAT]](
        grossReceiptsExcludingVATForm(years(turnoverSections6076)),
        formWithErrors =>
          BadRequest(
            view(
              formWithErrors,
              navigator.from
            )
          ),
        success => {
          val updatedSections =
            (success zip turnoverSections6076).map { case (grossReceipts, previousSection) =>
              previousSection.copy(grossReceiptsExcludingVAT = Some(grossReceipts))
            }

          val updatedData = updateAboutTheTradingHistoryPartOne(
            _.copy(
              turnoverSections6076 = Some(updatedSections)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(GrossReceiptsExcludingVatId, updatedData).apply(updatedData)))
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: Seq[TurnoverSection6076] => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistoryPartOne
      .flatMap(_.turnoverSections6076)
      .filter(_.nonEmpty)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show())))(action)

  private def years(turnoverSections6076: Seq[TurnoverSection6076]): Seq[String] =
    turnoverSections6076.map(_.financialYearEnd).map(_.getYear.toString)

}
