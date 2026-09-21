/*
 * Copyright 2026 HM Revenue & Customs
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

package test

import actions.SessionRequest
import controllers.lettingHistory.FiscalYearSupport
import models.ForType.FOR6048
import models.Session
import models.submissions.common.Address
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import util.DateUtilLocalised

import scala.language.implicitConversions

/**
  * @author Yuriy Tumakha
  */
abstract class FormSpec extends TCTRAppSpec:

  given DateUtilLocalised = inject[DateUtilLocalised]

  def sessionRequest(isWelsh: Boolean): SessionRequest[AnyContent] =
    SessionRequest[AnyContent](
      Session(
        referenceNumber = "99996048004",
        forType = FOR6048,
        address = Address("001", "GORING ROAD", "GORING-BY-SEA, WORTHING", "WEST SUSSEX", "BN12 4AX"),
        token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
        isWelsh = isWelsh
      ),
      FakeRequest("GET", "/")
    )

  trait SessionFixture(isWelsh: Boolean = false) extends FiscalYearSupport:
    given SessionRequest[AnyContent] = sessionRequest(isWelsh)
