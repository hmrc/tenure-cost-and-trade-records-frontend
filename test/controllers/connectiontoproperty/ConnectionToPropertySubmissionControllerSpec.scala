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

package controllers.connectiontoproperty

import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import connectors.Audit
import utils.TestBaseSpec

class ConnectionToPropertySubmissionControllerSpec extends TestBaseSpec {

  import TestData._

  val audit = mock[Audit]
  //doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])
  def connectionToPropertySubmissionController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  )         =
    new ConnectionToPropertySubmissionController(
      stubMessagesControllerComponents(),
      confirmationConnectionToProperty,
      audit,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "SUBMIT /" should {
    "return 302" in {
      val result = connectionToPropertySubmissionController().submit(fakeRequest)
      status(result) shouldBe Status.FOUND
    }
  }
}
