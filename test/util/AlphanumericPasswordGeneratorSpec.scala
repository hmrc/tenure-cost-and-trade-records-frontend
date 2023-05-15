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

package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import util.AlphanumericPasswordGenerator.passwordLength

/**
  * @author Yuriy Tumakha
  */
class AlphanumericPasswordGeneratorSpec extends AnyFlatSpec with should.Matchers {

  "AlphanumericPasswordGenerator" should s"generate passwords with length $passwordLength" in {
    AlphanumericPasswordGenerator.generatePassword should have length passwordLength
  }

  it                              should "generate password using only allowed digits and chars" in {
    AlphanumericPasswordGenerator.generatePassword should fullyMatch regex s"^[abcdefghjkmnpqrstuvwxyz23456789]{$passwordLength}$$"
  }

  it                              should "generate different password each time" in {
    AlphanumericPasswordGenerator.generatePassword should not be AlphanumericPasswordGenerator.generatePassword
  }

  it                              should "have at least 4 different chars in password" in {
    AlphanumericPasswordGenerator.generatePassword.toSet.size should be >= 4
  }

}
