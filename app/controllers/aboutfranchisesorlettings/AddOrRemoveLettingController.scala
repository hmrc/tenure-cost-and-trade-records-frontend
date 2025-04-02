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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.AddAnotherLettingForm.addAnotherLettingForm
import form.confirmableActionForm.confirmableActionForm
import models.submissions.aboutfranchisesorlettings.{ATMLetting, AboutFranchisesOrLettings, AdvertisingRightLetting, LettingPartOfProperty, OtherLetting, TelecomMastLetting}
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutFranchisesOrLettingsNavigator
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.addOrRemoveLetting as AddOrRemoveLettingView
import views.html.genericRemoveConfirmation as RemoveConfirmationView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddOrRemoveLettingController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  theListView: AddOrRemoveLettingView,
  theConfirmationView: RemoveConfirmationView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def franchisesOrLettingsData(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutFranchisesOrLettings] =
    request.sessionData.aboutFranchisesOrLettings

  private def getOperatorName(idx: Int)(implicit request: SessionRequest[AnyContent]): Option[String] =
    franchisesOrLettingsData.flatMap { fr =>
      fr.lettings.flatMap { lettings =>
        lettings.lift(idx).map {
          case atm: ATMLetting                 => atm.bankOrCompany.getOrElse("")
          case telecom: TelecomMastLetting     => telecom.operatingCompanyName.getOrElse("")
          case advert: AdvertisingRightLetting => advert.advertisingCompanyName.getOrElse("")
          case other: OtherLetting             => other.tenantName.getOrElse("")
        }
      }
    }

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val addAnother: Option[AnswersYesNo] =
      franchisesOrLettingsData
        .flatMap(_.lettings.flatMap(_.lift(index)).flatMap(_.addAnotherLetting))

    audit.sendChangeLink("AddOrRemoveLetting")

    Future.successful(
      Ok(
        theListView(
          addAnother.fold(addAnotherLettingForm)(addAnotherLettingForm.fill),
          index
        )
      )
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    val lettingsData          = request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettings)
    val numberOfLettings: Int =
      request.sessionData.aboutFranchisesOrLettings.map(_.lettings.getOrElse(IndexedSeq.empty).size).getOrElse(0)
    continueOrSaveAsDraft[AnswersYesNo](
      addAnotherLettingForm,
      formWithErrors =>
        BadRequest(
          theListView(
            formWithErrors,
            index
          )
        ),
      formData =>
        if (formData == AnswerYes && numberOfLettings >= 5 && navigator.from != "CYA") {
          Future.successful(Redirect(controllers.routes.MaxOfLettingsReachedController.show(Some("lettings"))))
        } else {
          lettingsData match {
            case Some(lettings) if lettings.isDefinedAt(index) =>
              val updatedLettings: IndexedSeq[LettingPartOfProperty] =
                lettings.updated(index, updateLettingWithNewAnswer(lettings(index), Some(formData)))
              val updatesSession                                     = AboutFranchisesOrLettings.updateAboutFranchisesOrLettings(about =>
                about.copy(lettings = Some(updatedLettings))
              )
              repository.saveOrUpdate(updatesSession).map { _ =>
                if (formData == AnswerYes) {
                  Redirect(routes.TypeOfLettingController.show(Some(index + 1)))
                } else {
                  Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
                }
              }
            case Some(lettings) if lettings.isEmpty            =>
              if (formData == AnswerYes) {
                Redirect(routes.TypeOfLettingController.show())
              } else {
                Redirect(routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())
              }
            case _                                             =>
              Future.successful(
                Redirect(
                  routes.AddOrRemoveLettingController.show(lettingsData.map(_.size).getOrElse(0))
                )
              )
          }
        }
    )
  }

  private def updateLettingWithNewAnswer(
    letting: LettingPartOfProperty,
    newAnswer: Option[AnswersYesNo]
  ): LettingPartOfProperty =
    letting match {
      case atm: ATMLetting                 => atm.copy(addAnotherLetting = newAnswer)
      case telecom: TelecomMastLetting     => telecom.copy(addAnotherLetting = newAnswer)
      case advert: AdvertisingRightLetting => advert.copy(addAnotherLetting = newAnswer)
      case other: OtherLetting             => other.copy(addAnotherLetting = newAnswer)
    }

  def remove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    getOperatorName(idx)
      .map { operatorName =>
        Future.successful(
          Ok(
            theConfirmationView(
              confirmableActionForm,
              operatorName,
              "label.section.aboutTheLettings",
              request.sessionData.toSummary,
              idx,
              controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.performRemove(idx),
              controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(idx)
            )
          )
        )
      }
      .getOrElse(Redirect(controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(0)))
  }

  def performRemove(idx: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      confirmableActionForm,
      formWithErrors =>
        getOperatorName(idx)
          .map { operatorName =>
            Future.successful(
              BadRequest(
                theConfirmationView(
                  formWithErrors,
                  operatorName,
                  "label.section.aboutTheLettings",
                  request.sessionData.toSummary,
                  idx,
                  controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.performRemove(idx),
                  controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(idx)
                )
              )
            )
          }
          .getOrElse(
            Redirect(controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(0))
          ),
      {
        case AnswerYes =>
          val aboutFranchisesOrLettings =
            request.sessionData.aboutFranchisesOrLettings.getOrElse(AboutFranchisesOrLettings())
          val lettings                  = aboutFranchisesOrLettings.lettings.getOrElse(IndexedSeq.empty)
          if (idx >= 0 && idx < lettings.length) {
            val updatedLettings = lettings.patch(idx, Nil, 1)
            val updatedAbout    = aboutFranchisesOrLettings.copy(lettings = Some(updatedLettings))
            repository.saveOrUpdate(request.sessionData.copy(aboutFranchisesOrLettings = Some(updatedAbout))).map { _ =>
              Redirect(
                controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController
                  .show(if (updatedLettings.isEmpty) 0 else updatedLettings.length - 1)
              )
            }
          } else {
            Future.successful(
              Redirect(controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(0))
            )
          }
        case AnswerNo  =>
          Redirect(controllers.aboutfranchisesorlettings.routes.AddOrRemoveLettingController.show(idx))
      }
    )
  }
}
