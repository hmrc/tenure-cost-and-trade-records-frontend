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

package form.lettingHistory

import form.AddressLine2Mapping.validateAddressLineTwo as line2
import form.BuildingNameNumberMapping.validateBuildingNameNumber as line1
import form.CountyMapping.validateCounty as county
import form.PostcodeMapping.postcode
import form.TownMapping.validateTown as town
import form.lettingHistory.FieldMappings.nonEmptyText
import models.submissions.lettingHistory.{Address, OccupierDetail}
import play.api.data.Form
import play.api.data.Forms.{mapping, optional}

object OccupierDetailForm:
  val theForm = Form(
    mapping(
      "name"    -> nonEmptyText(errorMessage = "lettingHistory.occupierDetail.name.required"),
      "address" -> mapping(
        "line1"    -> line1,
        "line2"    -> optional(line2),
        "town"     -> town,
        "county"   -> optional(county),
        "postcode" -> postcode(requiredError = "error.postcodeAlternativeContact.required")
      )(Address.apply)(Address.unapply)
    )((name, address) => OccupierDetail.apply(name, address, rental = None)) { obj =>
      val Some(name, address, _) = OccupierDetail.unapply(obj): @unchecked
      Some((name, address))
    }
  )
