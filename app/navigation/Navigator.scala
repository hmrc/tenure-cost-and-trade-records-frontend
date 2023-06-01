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

package navigation

import connectors.Audit
import controllers.routes
import models.Session
import navigation.identifiers.Identifier
import play.api.mvc.{AnyContent, Call, Request}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

abstract class Navigator @Inject() (
  audit: Audit
) {

  implicit class callHelpers(call: Call) {
    def urlWithoutIndex99: String            = call.url.replace("/99", "")
    def callWithSuffix(suffix: String): Call = call.copy(url = call.url + suffix)
  }

  val routeMap: Map[Identifier, Session => Call]

  def cyaPage: Option[Call] = None

  def postponeCYARedirectPages: Set[String] = Set.empty

  private val defaultPage: Session => Call = _ => routes.LoginController.show()

  def nextPage(id: Identifier, session: Session)(implicit
    hc: HeaderCarrier,
    request: Request[AnyContent]
  ): Session => Call =
    routeMap.getOrElse(id, defaultPage) andThen possibleCYARedirect andThen auditNextUrl(session)

  private def auditNextUrl(session: Session)(call: Call)(implicit hc: HeaderCarrier): Call = {
    audit.sendContinueNextPage(session, call.url)
    call
  }

  private def possibleCYARedirect(nextCall: Call)(implicit request: Request[AnyContent]): Call =
    cyaPage match {
      case Some(cyaCall) if from == "CYA" =>
        postponeCYARedirectPages
          .find(nextCall.url.contains)
          .fold(cyaCall)(_ => nextCall.callWithSuffix("?from=CYA"))
      case _                              => nextCall
    }

  private def from(implicit request: Request[AnyContent]): String =
    request.body.asFormUrlEncoded.flatMap(_.get("from").flatMap(_.headOption)).getOrElse("")

}
