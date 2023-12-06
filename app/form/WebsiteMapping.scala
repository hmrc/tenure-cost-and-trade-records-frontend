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

object WebsiteMapping {

  // Valid formats: test@test.com | test.test@hotmail.com | test.account@digital.gov.uk |

  val invalidWebaddressRegex =
    """^[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b(?:[-a-zA-Z0-9()@:%_\+.~#?&//=]*)$"""

  def validateWebaddress =
    text
      .verifying(Errors.webAddressBlank, wA => wA.nonEmpty)
      .verifying(
        Errors.webaddressFormat,
        wA => if (wA.nonEmpty) wA.matches(invalidWebaddressRegex) else true
      )

}
