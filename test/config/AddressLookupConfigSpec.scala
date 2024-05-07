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

package config

import play.api.i18n.Lang
import utils.TestBaseSpec

class AddressLookupConfigSpec extends TestBaseSpec {

  "AddressLookupConfig" should {
    "generate the correct config JSON with internationalized labels" in {
      val realMessagesApi = messagesApi

      val addressLookupConfig = new AddressLookupConfig(realMessagesApi)

      val continueUrl = "http://continue.url"
      val feedbackUrl = "http://feedback.url"

      implicit val lang: Lang = Lang("en") // or Lang("cy") for Welsh

      val configJson = addressLookupConfig.config(continueUrl, feedbackUrl)

      (configJson \ "version").as[Int] shouldBe 2

      val title = (configJson \ "labels" \ "en" \ "lookupPageLabels" \ "title").as[String]

      title shouldBe "What is your landlord''s address?"
    }
  }
}
