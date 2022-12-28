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

package navigation

import connectors.Audit
import models.Session
import models.submissions.Form6010._
import navigation.identifiers.{AreYouStillConnectedPageId, ConnectionToPropertyPageId, EditAddressPageId, Identifier}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import controllers.connectiontoproperty.routes
import models.submissions.connectiontoproperty.{AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress, StillConnectedDetails}

import scala.concurrent.ExecutionContext

class ConnectionToPropertyNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new ConnectionToPropertyNavigator(audit)

//  val userLoginDetails                  =
//    UserLoginDetails("testToken", "FOR6010", "123456", Address("13", Some("Street"), Some("City"), "AA11 1AA"))
  val stillConnectedDetailsYes  = Some(StillConnectedDetails(Some(AddressConnectionTypeYes)))
  val stillConnectedDetailsEdit = Some(StillConnectedDetails(Some(AddressConnectionTypeYesChangeAddress)))
  val stillConnectedDetailsNo   = Some(StillConnectedDetails(Some(AddressConnectionTypeNo)))

  val sessionYes = Session(testUserLoginDetails, stillConnectedDetailsYes)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "Connection to property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator.nextPage(UnknownIdentifier).apply(sessionYes) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to the type of connection to the property page when still connected has been selected and the selection is yes" in {
      navigator.nextPage(AreYouStillConnectedPageId).apply(sessionYes) mustBe routes.ConnectionToThePropertyController
        .show()
    }

    "return a function that goes to the edit address page when still connected has been selected and the selection is edit address" in {
      val sessionEdit = Session(testUserLoginDetails, stillConnectedDetailsEdit)
      navigator.nextPage(AreYouStillConnectedPageId).apply(sessionEdit) mustBe routes.EditAddressController.show()
    }

    "return a function that goes to the not connected page when still connected has been selected and the selection is no" in {
      val sessionNo = Session(testUserLoginDetails, stillConnectedDetailsNo)
      navigator.nextPage(AreYouStillConnectedPageId).apply(sessionNo) mustBe controllers.routes.PastConnectionController
        .show()
    }

    "return a function that goes to the type of connection to the property page when edit address has been completed" in {
      val sessionEdit = Session(testUserLoginDetails, stillConnectedDetailsEdit)
      navigator.nextPage(EditAddressPageId).apply(sessionEdit) mustBe routes.ConnectionToThePropertyController.show()
    }

    "return a function that goes to the task list page when connection to the property has been selected" in {
      navigator.nextPage(ConnectionToPropertyPageId).apply(sessionYes) mustBe controllers.routes.TaskListController
        .show()
    }
  }
}
