/*
 * Copyright 2022 HM Revenue & Customs
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

package actions.Form6010
import actions.Form6010.requests.SessionRequest6010
import config.ErrorHandler

import javax.inject.{Inject, Named}
import models.Form6010.Session6010
import play.api.libs.json.Reads
import play.api.mvc.Results._
import play.api.mvc._
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

case class With6010SessionRefiner @Inject()(
                                            errorHandler: ErrorHandler,
                                            @Named("session6010") val sessionRepository: SessionRepo
                                          )(implicit override val executionContext: ExecutionContext)
  extends ActionRefiner[Request, SessionRequest6010] {

  implicit def hc(implicit request: Request[_]): HeaderCarrier =
    HeaderCarrierConverter.fromRequestAndSession(request, request.session)
  override protected def refine[A](
                                    request: Request[A]): Future[Either[Result, SessionRequest6010[A]]] =
  {
    sessionRepository.get[Session6010](implicitly[Reads[Session6010]], hc(request)).map {
      case Some(s) =>
        Right(SessionRequest6010(sessionData = s, request = request))
      case None => Left(NotFound(errorHandler.notFoundTemplate(request)))
    }
  }

}
