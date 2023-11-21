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

package form

import play.api.data.Forms.text

object AlternativePhoneNumberMapping {

  // Valid formats: 1234567890 | +44 1234567890 | +44 0123 456 789 | +441234567890 | 0123 456 7890 | 0123-456-7890 | +44 123-456-7890

  val phoneNumberRegex = """^^[0-9\s\+()-]+$"""

  def validateAlternativePhoneNumber = {

    def validPNLength(pN: String) = pN.length >= 10 && pN.length <= 20

    text
      .verifying(Errors.contactAlternativePhoneRequired, pN => pN.nonEmpty)
      .verifying(Errors.contactPhoneLength, pN => if (pN.nonEmpty) validPNLength(pN) else true)
      .verifying(
        Errors.invalidPhone,
        pN => if (pN.nonEmpty && validPNLength(pN)) pN.matches(phoneNumberRegex) else true
      )
  }

}
