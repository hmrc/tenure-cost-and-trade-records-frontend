/*
 * Copyright 2023 HM Revenue & Customs
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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// */
//
//package controllers
//
//import connectors.Audit
//import controllers.FeedbackFormMapper.feedbackForm
//import form.Feedback
//import play.api.mvc._
//import repositories.SessionRepository
//import uk.gov.hmrc.mongo.cache.SessionCacheRepository
//import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
//import views.html.confirmation
//
//import javax.inject.{Inject, Singleton}
//import scala.concurrent.ExecutionContext
//
//@Singleton
//class ConfirmationController @Inject()(
//        mcc: MessagesControllerComponents,
//        auditService: Audit,
//        sessionRepository: SessionRepository,
//        confirmationView:confirmation,
//  )(implicit ec: ExecutionContext) extends FrontendController(mcc) {
//
//  import FeedbackFormMapper.feedbackForm
//
//  def show(): Action[AnyContent] = Action.async { implicit request =>
//    Ok(confirmationView(feedbackForm))
//    }
//
//  def feedbackSubmit(): Action[AnyContent] = Action.async { implicit request =>
//    feedbackForm.bindFromRequest().fold(
//      formWithErrors =>
//        sessionRepository.get[](ConfirmationController.SubmittedChallengeKey).map{
//          case Some(sc) =>
//            Ok(confirmationView(formWithErrors))
//          case _ =>
//            NotFound(errorView(404))
//        },
//      feedbackForm => {
//        sendFeedback(feedbackForm).map{ _ =>
//          Redirect(routes.FeedbackController.feedbackThx)
//        }
//      }
//    )
//  }
//
//  private def sendFeedback(f: Feedback)(implicit request: Request[_]) = {
//    auditService("Feedback", Map("comments" -> f.comments.getOrElse(""), "satisfaction" -> f.rating.get))
//  }
//
//}
//
//object ConfirmationController {
//  val SubmittedChallengeKey = "SUBMITTED_TCTR_FEEDBACK"
//}
