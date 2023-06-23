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

package crypto

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import play.api.libs.json.{Json, OFormat, Writes}
import SensitiveFormats._
import utils.SensitiveTestHelper

import java.io.File

class SensitiveFormatsSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper{
  import SensitiveFormatsSpec._

  val testConfig: Configuration = loadTestConfig()

  implicit val crypto: MongoCrypto = new TestMongoCrypto(testConfig)

  "SensitiveFormats" should {

    "serialize and deserialize SensitiveString correctly" in {
      val originalString = "mySensitiveData"
      val sensitiveString = SensitiveString(originalString)

      val json = Json.toJson(sensitiveString)
      val deserialized = Json.fromJson[SensitiveString](json)

      deserialized.get shouldBe sensitiveString
      deserialized.get.decryptedValue shouldBe originalString
    }

    "serialize and deserialize SensitiveTestEntity correctly" in {
      val originalString = "mySensitiveData"
      val sensitiveString = SensitiveString(originalString)
      val testEntity = SensitiveTestEntity("normalString", sensitiveString)

      val json = Json.toJson(testEntity)
      val deserialized = Json.fromJson[SensitiveTestEntity](json)

      deserialized.get shouldBe testEntity
      deserialized.get.encryptedString shouldBe sensitiveString
    }

  }
}

object SensitiveFormatsSpec {

  case class SensitiveTestEntity(
                                  normalString    : String,
                                  encryptedString : SensitiveString
                                )

  object SensitiveTestEntity {
    import crypto.SensitiveFormats._
    implicit def format(implicit crypto: MongoCrypto): OFormat[SensitiveTestEntity] = Json.format[SensitiveTestEntity]
  }

}
