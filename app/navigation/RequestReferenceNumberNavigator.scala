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
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class RequestReferenceNumberNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController.show())

  override val routeMap: Map[Identifier, Session => Call] = Map(
    NoReferenceNumberPageId                      -> (_ =>
      controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController.show()
    ),
    NoReferenceNumberContactDetailsPageId        -> (_ =>
      controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController.show()
    ),
    CheckYourAnswersRequestReferenceNumberPageId -> (_ =>
      controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController.confirmation()
    )
  )

}
