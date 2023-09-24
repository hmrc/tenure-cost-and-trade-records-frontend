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

import play.api.Configuration
import uk.gov.hmrc.crypto.{Hasher, OnewayCryptoFactory, PlainText, Scrambled, Verifier}

import javax.inject.{Inject, Singleton}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class MongoHasher @Inject() (configuration: Configuration) extends Hasher with Verifier {

  val hasherCrypto = OnewayCryptoFactory.shaCryptoFromConfig("oneway.hash", configuration.underlying)

  def hash(plainText: PlainText): Scrambled = hasherCrypto.hash(plainText)

  def verify(sample: PlainText, ncrypted: Scrambled): Boolean = hasherCrypto.verify(sample, ncrypted)

  def hash(str: String): String = hash(PlainText(str)).value

  def verify(sample: String, hash: String): Boolean = verify(PlainText(sample), Scrambled(hash))

}
