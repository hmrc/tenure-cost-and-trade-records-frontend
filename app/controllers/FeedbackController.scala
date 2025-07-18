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

package controllers

import actions.WithSessionRefiner
import connectors.Audit
import form.Feedback
import models.Session
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.AddressConnectionType.*
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.i18n.I18nSupport
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.confirmation
import views.html.feedback.{feedback, feedbackThx}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class FeedbackController @Inject() (
  mcc: MessagesControllerComponents,
  feedbackView: feedback,
  feedbackThxView: feedbackThx,
  confirmationConnectedView: confirmation,
  withSessionRefiner: WithSessionRefiner,
  audit: Audit
) extends FrontendController(mcc)
    with I18nSupport {

  import FeedbackFormMapper.feedbackForm

  def feedback: Action[AnyContent] = Action { implicit request =>
    Ok(feedbackView(feedbackForm))
  }

  def feedbackThx: Action[AnyContent] = Action { implicit request =>
    Ok(feedbackThxView())
  }

  def feedbackSubmitWithoutSession: Action[AnyContent] = Action.async { implicit request =>
    feedbackForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful {
            BadRequest(feedbackView(formWithErrors))
          },
        feedbackForm => {
          sendFeedback("InPageFeedback", feedbackForm)
          Future.successful(Redirect(routes.FeedbackController.feedbackThx))
        }
      )
  }

  def feedbackSubmit: Action[AnyContent] = Action.async { implicit request =>
    if (request.session.get("session").isEmpty) {
      feedbackSubmitWithoutSession.apply(request) // need that if user is not logged in
    } else {
      (Action andThen withSessionRefiner)
        .async { implicit request =>
          feedbackForm
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful {
                  BadRequest(feedbackView(formWithErrors))
                },
              feedbackForm => {
                sendFeedback("InPageFeedback", feedbackForm, request.sessionData)
                Future.successful(Redirect(routes.FeedbackController.feedbackThx))
              }
            )
        }
        .apply(request)
    }
  }

  def feedbackSharedSubmit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    feedbackForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful {
            BadRequest(confirmationConnectedView(formWithErrors))
          },
        feedbackForm => {
          val addressConnectionType  = request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType)
          val vacantPropertySelected = request.sessionData.stillConnectedDetails.flatMap(_.isPropertyVacant)

          if (addressConnectionType.contains(AddressConnectionTypeNo)) {
            sendFeedback("NotConnectedFeedback", feedbackForm, request.sessionData)
            Future.successful(Redirect(routes.FeedbackController.feedbackThx))
          } else if (vacantPropertySelected.contains(AnswerYes)) {
            sendFeedback("VacantPropertyFeedback", feedbackForm, request.sessionData)
            Future.successful(Redirect(routes.FeedbackController.feedbackThx))
          } else {
            sendFeedback("PostSubmitFeedback", feedbackForm, request.sessionData)
            Future.successful(Redirect(routes.FeedbackController.feedbackThx))
          }
        }
      )
  }

  def feedbackRequestReferenceNumber: Action[AnyContent] = Action.async { implicit request =>
    feedbackForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful {
            BadRequest(feedbackView(formWithErrors))
          },
        feedbackForm => {
          sendFeedback("NoReferenceFeedback", feedbackForm)
          Future.successful(Redirect(routes.FeedbackController.feedbackThx))
        }
      )
  }

  private def sendFeedback(eventName: String, f: Feedback)(implicit request: Request[?]) =
    audit(eventName, Map("comments" -> f.comments.getOrElse(""), "satisfaction" -> f.rating.get))

  private def sendFeedback(eventName: String, f: Feedback, session: Session)(implicit request: Request[?]) =
    audit(
      eventName,
      Map(
        "comments"        -> f.comments.getOrElse(""),
        "satisfaction"    -> f.rating.get,
        "referenceNumber" -> session.referenceNumber,
        "forType"         -> session.forType.toString
      )
    )

}

object FeedbackFormMapper {
  val feedbackForm: Form[Feedback] = Form(
    mapping(
      "feedback-rating"   -> optional(text).verifying("feedback.rating.required", _.isDefined),
      "feedback-comments" -> optional(text).verifying("feedback.comments.maxLength", it => it.forall(_.length <= 2000))
    )(Feedback.apply)(o => Some(Tuple.fromProductTyped(o)))
  )
}
