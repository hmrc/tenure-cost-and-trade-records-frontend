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

object AddressLine2Mapping {

  def validateAddressLineTwo = {

    val invalidCharRegex = """^[0-9A-Za-z\s\-\,]+$"""

    def validddressLineTwo(aLT: String) = aLT.length <= 50

    text
      .verifying(Errors.addressBuildingNameNumberRequired, aLT => aLT.nonEmpty)
      .verifying(Errors.addressLine2Length, aLT => if aLT.nonEmpty then validddressLineTwo(aLT) else true)
      .verifying(
        Errors.invalidCharAddress2,
        aLT => if aLT.nonEmpty && validddressLineTwo(aLT) then aLT.matches(invalidCharRegex) else true
      )
  }

}
