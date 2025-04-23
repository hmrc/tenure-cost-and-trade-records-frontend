/*
 * Copyright 2025 HM Revenue & Customs
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

package models.submissions.downloadFORTypeForm

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import utils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class DownloadPDFDetailsSpec extends AnyFlatSpec with should.Matchers with FakeObjects:

  "DownloadPDFDetails model" should "be serialized/deserialized from json" in {
    val json = Json.toJson(prefilledDownloadPDFRef)
    json.as[DownloadPDFDetails] shouldBe prefilledDownloadPDFRef
    Json.stringify(
      json
    )                           shouldBe """{"referenceNumber":{"value":"99996010004"},"downloadPDF":{"downloadPDF":"FOR6010"}}"""
  }
