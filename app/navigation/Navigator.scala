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

package navigation

import connectors.Audit
import controllers.routes
import models.Session
import navigation.UrlHelpers.urlPlusParamPrefix
import navigation.identifiers.Identifier
import play.api.mvc.{AnyContent, Call, Request}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

abstract class Navigator @Inject() (
  audit: Audit
) {

  implicit class callHelpers(call: Call) {
    def callWithParam(paramAndValue: String): Call = call.copy(url = urlPlusParamPrefix(call.url) + paramAndValue)
  }

  val routeMap: Map[Identifier, Session => Call]

  def cyaPage: Option[Call] = None

  /**
    * Postpone redirect to CYA in case the next page is in this Set.
    */
  def postponeCYARedirectPages: Set[String] = Set.empty

  /**
    * Redirect to another page (value in Map) instead of back to CYA in case the next page is a key in Map.
    */
  def overrideRedirectIfFromCYA: Map[String, Session => Call] = Map.empty

  private val defaultPage: Session => Call = _ => routes.LoginController.show

  /**
    * Get next page ignoring redirect back to CYA.
    */
  def nextWithoutRedirectToCYA(id: Identifier, session: Session)(implicit hc: HeaderCarrier): Session => Call =
    routeMap.getOrElse(id, defaultPage) andThen auditNextUrl(session)

  /**
    * Get next page with possible redirect back to CYA.
    */
  def nextPage(id: Identifier, session: Session)(implicit
    hc: HeaderCarrier,
    request: Request[AnyContent]
  ): Session => Call =
    routeMap.getOrElse(id, defaultPage) andThen possibleCYARedirect(session: Session) andThen auditNextUrl(session)

  protected def auditNextUrl(session: Session)(call: Call)(implicit hc: HeaderCarrier): Call = {
    audit.sendContinueNextPage(session, call.url)
    call
  }

  private def possibleCYARedirect(session: Session)(nextCall: Call)(implicit request: Request[AnyContent]): Call =
    if (from == "CYA") {
      overrideRedirectIfFromCYA
        .find(entry => nextCall.url.contains(entry._1))
        .map(_._2(session))
        .orElse(
          postponeCYARedirectPages
            .find(nextCall.url.contains)
            .map(_ => nextCall.callWithParam("from=CYA"))
        )
        .orElse(cyaPage)
        .getOrElse(nextCall)
    } else {
      nextCall
    }

  def idx(implicit request: Request[AnyContent]): Int =
    request
      .getQueryString("idx")
      .orElse(request.body.asFormUrlEncoded.flatMap(_.get("idx").flatMap(_.headOption)))
      .fold(0)(_.toInt)

  def from(implicit request: Request[AnyContent]): String =
    request
      .getQueryString("from")
      .getOrElse(request.body.asFormUrlEncoded.flatMap(_.get("from").flatMap(_.headOption)).getOrElse(""))
}
