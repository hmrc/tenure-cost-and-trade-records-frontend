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

package navigation.lettingHistory

import actions.SessionRequest
import app.RoutesPrefix
import connectors.Audit
import models.submissions.lettingHistory.SessionWrapper
import navigation.identifiers.Identifier as PageIdentifier
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Call, Result}
import uk.gov.hmrc.http.HeaderCarrier

abstract class Navigator(audit: Audit):

  type NavigationData     = Map[String, String | Int | Boolean]
  type NavigationFunction = (SessionWrapper, NavigationData) => Option[Call]
  type NavigationMap      = Map[PageIdentifier, NavigationFunction]

  val backwardNavigationMap: NavigationMap
  val checkYourAnswerCall: Call
  val taskListCall: Call

  def backLinkUrl(ofPage: PageIdentifier, navigation: NavigationData = Map.empty)(using
    request: SessionRequest[AnyContent]
  ): Option[String] =
    (
      if request.isFromCheckYourAnswer
      then Some(checkYourAnswerCall)
      else if request.isFromTaskList
      then Some(taskListCall)
      else
        backwardNavigationMap
          .get(ofPage)
          .flatMap(_.apply(SessionWrapper(request.sessionData, changed = false), navigation))
    ) .map(c => toDecorated(c).toString)

  val forwardNavigationMap: NavigationMap

  def redirect(currentPage: PageIdentifier, session: SessionWrapper, navigation: NavigationData = Map.empty)(using
    hc: HeaderCarrier,
    request: SessionRequest[AnyContent]
  ): Result =
    val nextCall =
      (
        if request.isFromCheckYourAnswer && session.notChanged
        then Some(checkYourAnswerCall)
        else if request.isFromTaskList && session.notChanged
        then Some(taskListCall)
        else
          for call <- forwardNavigationMap(currentPage)(session, navigation)
          yield call
      ) .map(c => toDecorated(c))

    audit.sendContinueNextPage(session.data, nextCall.toString)
    nextCall match
      case Some(call) => Redirect(call)
      case _          => throw new Exception("NavigatorIllegalState : couldn't determine next redirect call")

  // This helper method makes sure that the call is properly prefixed
  // and completed with the right fragment
  protected def toDecorated(call: Call)(using request: SessionRequest[AnyContent]) =
    val prefixedCall =
      if !call.url.startsWith(RoutesPrefix.prefix)
      then Call(call.method, RoutesPrefix.prefix + call.url, call.fragment)
      else call
    prefixedCall.withFragment {
      request.eventualFromFragment.getOrElse(prefixedCall.fragment)
    }
