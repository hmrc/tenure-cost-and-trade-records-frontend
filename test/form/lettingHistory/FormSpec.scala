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

package form.lettingHistory

import actions.SessionRequest
import models.Session
import models.ForType.FOR6048
import models.submissions.common.Address
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.Lang.defaultLang
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContent
import play.api.test.{FakeRequest, Injecting}
import util.DateUtilLocalised

class FormSpec extends AnyFlatSpec with Matchers with OptionValues with GuiceOneAppPerSuite with Injecting:

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"                         -> false,
        "metrics.enabled"                     -> false,
        "create-internal-auth-token-on-start" -> false,
        "urls.tctrFrontend"                   -> "someUrl"
      )
      .build()

  given Messages          = inject[MessagesApi].preferred(Seq(defaultLang))
  given DateUtilLocalised = inject[DateUtilLocalised]

  def sessionRequest(isWelsh: Boolean) =
    SessionRequest[AnyContent](
      Session(
        referenceNumber = "99996010004",
        forType = FOR6048,
        address = Address("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
        token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
        isWelsh = isWelsh
      ),
      FakeRequest("GET", "/")
    )
