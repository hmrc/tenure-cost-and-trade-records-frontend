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
import navigation.identifiers.{AreYouStillConnectedPageId, ConnectionToPropertyPageId, EditAddressPageId, Identifier}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import controllers.connectiontoproperty.routes

import scala.concurrent.ExecutionContext

class ConnectionToPropertyNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new ConnectionToPropertyNavigator(audit)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "Connection to property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to the type of connection to the property page when still connected has been selected and the selection is yes" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe routes.ConnectionToThePropertyController
        .show()
    }

    "return a function that goes to the edit address page when still connected has been selected and the selection is edit address" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) mustBe routes.EditAddressController.show()
    }

    "return a function that goes to the not connected page when still connected has been selected and the selection is no" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsNoSession)
        .apply(stillConnectedDetailsNoSession) mustBe controllers.notconnected.routes.PastConnectionController
        .show()
    }

    "return a function that goes to the type of connection to the property page when edit address has been completed" in {
      navigator
        .nextPage(EditAddressPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) mustBe routes.ConnectionToThePropertyController.show()
    }

    "return a function that goes to the task list page when connection to the property has been selected" in {
      navigator
        .nextPage(ConnectionToPropertyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe controllers.routes.TaskListController
        .show()
    }
  }
}
