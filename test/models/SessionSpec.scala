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

package models

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import utils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class SessionSpec extends AnyFlatSpec with Matchers with FakeObjects:

  "Session" should "be serialized/deserialized from JSON - 6010" in {
    val json = Json.toJson(aboutYourTradingHistory6010YesSession)
    json.as[Session] shouldBe aboutYourTradingHistory6010YesSession
  }

  it should "be serialized/deserialized from JSON - 6020" in {
    val json = Json.toJson(prefilledFull6020Session)
    json.as[Session] shouldBe prefilledFull6020Session
  }

  it should "be serialized/deserialized from JSON - 6030" in {
    val json = Json.toJson(prefilledFull6030Session)
    json.as[Session] shouldBe prefilledFull6030Session
  }

  it should "be serialized/deserialized from JSON - 6045 Trading History" in {
    val json = Json.toJson(aboutYourTradingHistory6045YesSession)
    json.as[Session] shouldBe aboutYourTradingHistory6045YesSession
  }

  it should "be serialized/deserialized from JSON - 6045 Franchise or Letting" in {
    val json = Json.toJson(sessionAboutFranchiseOrLetting6045)
    json.as[Session] shouldBe sessionAboutFranchiseOrLetting6045
  }

  it should "be serialized/deserialized from JSON - 6048" in {
    val json = Json.toJson(aboutYouAndTheProperty6048YesSession)
    json.as[Session] shouldBe aboutYouAndTheProperty6048YesSession
  }

  it should "be serialized/deserialized from JSON - 6076" in {
    val json = Json.toJson(aboutYourTradingHistory6076YesSession)
    json.as[Session] shouldBe aboutYourTradingHistory6076YesSession
  }
