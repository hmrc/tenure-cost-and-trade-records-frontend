/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package controllers

import connectors.Audit
import controllers.FeedbackFormMapper.feedbackForm
import form.Feedback
import play.api.mvc._
import repositories.SessionRepository
import uk.gov.hmrc.mongo.cache.SessionCacheRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.confirmation

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ConfirmationController @Inject()(
        mcc: MessagesControllerComponents,
        auditService: Audit,
        sessionRepository: SessionRepository,
        confirmationView:confirmation,
  )(implicit ec: ExecutionContext) extends FrontendController(mcc) {

  import FeedbackFormMapper.feedbackForm

  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    Ok(confirmationView(feedbackForm))
    }

  def feedbackSubmit(): Action[AnyContent] = Action.async { implicit request =>
    feedbackForm.bindFromRequest().fold(
      formWithErrors =>
        sessionRepository.get[](ConfirmationController.SubmittedChallengeKey).map{
          case Some(sc) =>
            Ok(confirmationView(formWithErrors))
          case _ =>
            NotFound(errorView(404))
        },
      feedbackForm => {
        sendFeedback(feedbackForm).map{ _ =>
          Redirect(routes.FeedbackController.feedbackThx)
        }
      }
    )
  }

  private def sendFeedback(f: Feedback)(implicit request: Request[_]) = {
    auditService("Feedback", Map("comments" -> f.comments.getOrElse(""), "satisfaction" -> f.rating.get))
  }

}

object ConfirmationController {
  val SubmittedChallengeKey = "SUBMITTED_TCTR_FEEDBACK"
}
