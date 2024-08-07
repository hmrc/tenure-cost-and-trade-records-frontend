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

package form

import play.api.data.Forms.text

object CountyMapping {

  def validateCounty = {

    val invalidCharRegex = """^[0-9A-Za-z\s\-\,]+$"""

    def validCountyLength(county: String) = county.length <= 50

    text
      .verifying(Errors.addressCountyRequired, county => county.nonEmpty)
      .verifying(Errors.addressCountyLength, county => if county.nonEmpty then validCountyLength(county) else true)
      .verifying(
        Errors.invalidCharAddressCounty,
        county => if county.nonEmpty && validCountyLength(county) then county.matches(invalidCharRegex) else true
      )
  }

}
