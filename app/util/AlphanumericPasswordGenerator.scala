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

import java.security.SecureRandom

/**
  * Secure alphanumeric password generator.
  *
  * @author Yuriy Tumakha
  */
object AlphanumericPasswordGenerator {

  val passwordLength: Int               = 8
  private val validDigits               = (2 to 9).map(_.toString.head)
  private val validChars                = "abcdefghjkmnpqrstuvwxyz".toCharArray
  private val allowedChars: Array[Char] = validChars ++ validDigits
  private val maxIndex                  = allowedChars.length
  private val random                    = new SecureRandom

  def generatePassword: String =
    new String(
      Array.fill(passwordLength)(allowedChars(random nextInt maxIndex))
    )

}
