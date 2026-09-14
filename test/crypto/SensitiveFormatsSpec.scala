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

package crypto

import crypto.SensitiveFormats.*
import org.bson.types.ObjectId
import play.api.libs.json.{Json, OFormat}
import test.SensitiveTestHelper
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import uk.gov.hmrc.vo.unit.test.BaseSpec

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.language.implicitConversions

class SensitiveFormatsSpec extends BaseSpec with SensitiveTestHelper:

  import SensitiveTestEntity.*

  "SensitiveFormats" should {
    "serialize and deserialize SensitiveString correctly" in {
      val originalString  = "mySensitiveData"
      val sensitiveString = SensitiveString(originalString)

      val json         = Json.toJson(sensitiveString)
      val deserialized = Json.fromJson[SensitiveString](json)

      deserialized.get                shouldBe sensitiveString
      deserialized.get.decryptedValue shouldBe originalString
    }

    "serialize and deserialize SensitiveTestEntity correctly" in {
      val originalString  = "mySensitiveData"
      val sensitiveString = SensitiveString(originalString)
      val testEntity      = SensitiveTestEntity("normalString", sensitiveString)

      val json         = Json.toJson(testEntity)
      val deserialized = Json.fromJson[SensitiveTestEntity](json)

      deserialized.get                 shouldBe testEntity
      deserialized.get.encryptedString shouldBe sensitiveString
    }
  }

  case class SensitiveTestEntity(
    normalString: String,
    encryptedString: SensitiveString,
    _id: ObjectId = ObjectId.get(),
    createdAt: Instant = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  )

  object SensitiveTestEntity:

    import uk.gov.hmrc.mongo.play.json.formats.MongoFormats.Implicits.*
    import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats.Implicits.*

    implicit def format(using crypto: MongoCrypto): OFormat[SensitiveTestEntity] = Json.format
