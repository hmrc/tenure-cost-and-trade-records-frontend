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

package security

import connectors.Document
import models.FORLoginResponse
import models.submissions.common.Address
//import models.serviceContracts.submissions.Address
import org.joda.time.DateTime
import uk.gov.hmrc.http.HeaderCarrier
import utils.UnitTest

import scala.concurrent.ExecutionContext.Implicits.global

class LoginToBackendSpec extends UnitTest {
  import TestData._

  type ReferenceNumber = String

  "Login to HOD with valid credentials" when {
    implicit val hc = HeaderCarrier()

    "there is no previously stored document" should {
      var updated: (HeaderCarrier, ReferenceNumber, Document) = null
      val l                                                   = LoginToBackend(
        respondWith(refNum, postcode)(loginResponse)
//        none,
//        set[HeaderCarrier, ReferenceNumber, Document, Unit](updated = _)
      ) _
      val r                                                   = await(l(refNum, postcode, now))

      "indicate there is no saved document" in {
        assert(
          r.leftSideValue === NoExistingDocument(
            loginResponse.forAuthToken,
            loginResponse.forType,
            loginResponse.address
          )
        )
      }

//       "loads an empty document with the retrieved credentials into the session" in {
//         set[HeaderCarrier, ReferenceNumber, Document, Unit](updated = _)
//         assert(updated === ((hc, refNum, Document(refNum, now, address = Some(loginResponse.address)))))
//       }
    }
  }

  object TestData {
    val refNum        = s"1111111899"
    val password      = "aljsljdf"
    val postcode      = "CV24 5RR"
    val testAddress   = Address("123", None, None, postcode)
    val forType       = "FOR6010"
    val auth          = "YouAreLoggedInNow"
    val loginResponse = FORLoginResponse(auth, forType, testAddress)
    val now           = new DateTime(2015, 3, 2, 13, 20)
    val savedDoc      = Document("savedDocument", now)
  }
}

case class ArgumentsDidNotMatch(es: Seq[Any], as: Seq[Any]) extends Exception(s"Expected: $es but got: $as")
