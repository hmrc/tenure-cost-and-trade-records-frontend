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

package actions

import models.Session
import play.api.libs.json.Reads
import play.api.mvc.Results.TemporaryRedirect
import play.api.mvc.{ActionRefiner, Request, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

case class WithSessionRefiner @Inject() (
  @Named("session") val sessionRepository: SessionRepo
)(implicit override val executionContext: ExecutionContext)
    extends ActionRefiner[Request, SessionRequest] {

  implicit def hc(implicit request: Request[?]): HeaderCarrier =
    HeaderCarrierConverter.fromRequestAndSession(request, request.session)

  override protected def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
    sessionRepository.get(using implicitly[Reads[Session]], hc(using request)).map {
      case Some(s) => Right(actions.SessionRequest(sessionData = s, request = request))
      case None    => Left(TemporaryRedirect(controllers.routes.LoginController.show.url))
    }

}
