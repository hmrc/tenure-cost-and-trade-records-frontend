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

package security

import models.ForType.*
import models.FORLoginResponse
import models.submissions.common.Address
import security.LoginToBackend.{Postcode, RefNumber, StartTime}
import utils.UnitTest

import java.time.{ZoneOffset, ZonedDateTime}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginToBackendSpec extends UnitTest {
  import TestData._

  type ReferenceNumber = String

  "Login to HOD with valid credentials" when {

    "there is no previously stored document" should {
      val l: (RefNumber, Postcode, StartTime) => Future[LoginResult] = LoginToBackend(
        respondWith(refNum, postcode)(loginResponse)
      )
      val r                                                          = await(l(refNum, postcode, now))

      "indicate there is no saved document" in
        assert(
          r === NoExistingDocument(
            loginResponse.forAuthToken,
            loginResponse.forType,
            loginResponse.address,
            loginResponse.isWelsh
          )
        )
    }
  }

  object TestData {
    val refNum        = s"1111111899"
    val password      = "aljsljdf"
    val postcode      = "CV24 5RR"
    val testAddress   = Address("123", None, "test", None, postcode)
    val forType       = FOR6010.toString
    val auth          = "YouAreLoggedInNow"
    val loginResponse = FORLoginResponse(auth, forType, testAddress, isWelsh = false)
    val now           = ZonedDateTime.of(2015, 3, 2, 13, 20, 0, 0, ZoneOffset.UTC)
  }
}

case class ArgumentsDidNotMatch(es: Seq[Any], as: Seq[Any]) extends Exception(s"Expected: $es but got: $as")
