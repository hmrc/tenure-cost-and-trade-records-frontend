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
import models.Session
import navigation.identifiers.{Identifier, PastConnectionId, RemoveConnectionId}
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class RemoveConnectionNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] = Some(
    controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show()
  )

  override val overrideRedirectIfFromCYA: Map[String, Session => Call] = Map(
    (
      controllers.notconnected.routes.PastConnectionController.show().url,
      _ => controllers.notconnected.routes.PastConnectionController.show()
    )
  )

  override val routeMap: Map[Identifier, Session => Call] = Map(
    PastConnectionId   -> (_ => controllers.notconnected.routes.RemoveConnectionController.show()),
    RemoveConnectionId -> (_ => controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show())
  )
}
