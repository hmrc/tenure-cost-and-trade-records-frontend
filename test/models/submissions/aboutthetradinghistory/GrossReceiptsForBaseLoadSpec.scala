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

package models.submissions.aboutthetradinghistory

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class GrossReceiptsForBaseLoadSpec extends PlaySpec {
  "GrossReceiptsForBaseLoad" should {
    "serialize and deserialize correctly" in {
      val grossReceiptsForBaseLoadSpec = GrossReceiptsForBaseLoad(
        Some(1),
        Some(2),
        Some(3),
        Some(4),
        Some(5)
      )
      val json                         = Json.toJson(grossReceiptsForBaseLoadSpec: GrossReceiptsForBaseLoad)
      json.as[GrossReceiptsForBaseLoad] mustBe grossReceiptsForBaseLoadSpec
    }

    "have correct total value" in {
      val grossReceiptsForBaseLoadSpec = GrossReceiptsForBaseLoad(
        Some(1),
        Some(2),
        Some(3),
        Some(4),
        Some(5)
      )
      grossReceiptsForBaseLoadSpec.total mustBe 15
    }
  }
}
