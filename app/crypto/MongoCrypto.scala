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

package crypto

import play.api.Configuration
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, PlainBytes, PlainContent, PlainText, SymmetricCryptoFactory}

import javax.inject.{Inject, Singleton}

@Singleton
class MongoCrypto @Inject() (configuration: Configuration) extends Encrypter with Decrypter {

  private val symmetricCrypto: Encrypter & Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  override def encrypt(plain: PlainContent): Crypted = symmetricCrypto.encrypt(plain)

  override def decrypt(reversiblyEncrypted: Crypted): PlainText = symmetricCrypto.decrypt(reversiblyEncrypted)

  override def decryptAsBytes(reversiblyEncrypted: Crypted): PlainBytes =
    symmetricCrypto.decryptAsBytes(reversiblyEncrypted)
}
