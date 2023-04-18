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

package controllers

import actions.SessionRequest
import play.api.data.Form
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

/**
  * All controllers that capture data and store it in session must inherit this base class.
  *
  * @author Yuriy Tumakha
  */
abstract class FORDataCaptureController(cc: MessagesControllerComponents) extends FrontendController(cc) {

  implicit def toFut[A](a: A): Future[A] = Future.successful(a)

  def continueOrSaveAsDraft[T](form: Form[T], hasErrors: Form[T] => Future[Result], success: T => Future[Result])(
    implicit request: SessionRequest[AnyContent]
  ): Future[Result] =
    form
      .bindFromRequest()
      .fold(
        if (isSaveAsDraft) _ => saveAsDraftRedirect else hasErrors,
        success.andThen(if (isSaveAsDraft) saveAsDraftRedirect else _)
      )

  def continueOrSaveAsDraft(successResult: Future[Result])(implicit
    request: SessionRequest[AnyContent]
  ): Future[Result] =
    if (isSaveAsDraft) saveAsDraftRedirect else successResult

  private def isSaveAsDraft(implicit request: SessionRequest[AnyContent]) =
    request.body.asFormUrlEncoded.flatMap(_.get("save_button")).isDefined

  private def saveAsDraftRedirect(implicit request: Request[_]): Result = Redirect(
    controllers.routes.SaveAsDraftController.customPassword(request.path)
  )

}
