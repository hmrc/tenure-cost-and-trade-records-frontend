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
import play.api.mvc.Call
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.ExecutionContext

abstract class Navigator @Inject() (
  audit: Audit
)(implicit ec: ExecutionContext) {

  val routeMap: Map[Identifier, Session => Call]

  def nextPage(id: Identifier)(implicit hc: HeaderCarrier): Session => Call = (
    routeMap.getOrElse(id, (_: Session) => routes.LoginController.show())
  ) andThen auditNextUrl

  private def auditNextUrl(call: Call)(implicit hc: HeaderCarrier): Call = {
    audit.sendContinueNextPage(call.url)
    call
  }
}
