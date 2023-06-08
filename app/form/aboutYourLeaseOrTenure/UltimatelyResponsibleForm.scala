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

package form.aboutYourLeaseOrTenure

import form.MappingSupport.{buildingInsuranceType, insideRepairsType, outsideRepairsType}
import models.submissions.aboutYourLeaseOrTenure.UltimatelyResponsible
import models.submissions.common.{BuildingInsuranceBoth, BuildingInsuranceLandlord, InsideRepairsBoth, InsideRepairsLandlord, InsideRepairsTenant, OutsideRepairsBoth, OutsideRepairsLandlord, OutsideRepairsTenant}
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import uk.gov.voa.play.form.ConditionalMappings.{isEqual, mandatoryIf, mandatoryIfAnyAreTrue, mandatoryIfEqual, mandatoryIfEqualToAny, mandatoryIfTrue}

object UltimatelyResponsibleForm {

  val ultimatelyResponsibleForm = Form(
    mapping(
      "outsideRepairs"         -> outsideRepairsType,
      "insideRepairs"          -> insideRepairsType,
      "buildingInsurance"      -> buildingInsuranceType,
      "sharedResponsibilities" -> mandatoryIfAnyAreTrue(
        Seq("true"),
        default(text, "").verifying(
          nonEmpty(errorMessage = "error.sharedResponsibilities.required"),
          maxLength(2000, "error.sharedResponsibilities.maxLength")
        )
      )
    )(UltimatelyResponsible.apply)(UltimatelyResponsible.unapply)
  )

}
//optional(
//  default(text, "").verifying(
//    maxLength(2000, "error.sharedResponsibilities.maxLength")
//  ))

//if(OutsideRepairsBoth.name == "both" || InsideRepairsBoth.name == "both" || BuildingInsuranceBoth.name == "both")

//optional(
//  if(OutsideRepairsBoth.name.equals("both") || InsideRepairsBoth.name.equals("both") || BuildingInsuranceBoth.name.equals("both"))
//    default(text, "").verifying(
//      nonEmpty(errorMessage = "error.sharedResponsibilities.required"),
//      maxLength(2000, "error.sharedResponsibilities.maxLength")
//    ) else
//    default(text, "").verifying(
//      maxLength(2000, "error.sharedResponsibilities.maxLength")
//    )
//)

//mandatoryIfEqualToAny(
//  "sharedResponsibilities",
//  Seq(OutsideRepairsBoth.name, InsideRepairsBoth.name, BuildingInsuranceBoth.name),
//  default(text, "").verifying(
//    nonEmpty(errorMessage = "error.sharedResponsibilities.required"),
//    maxLength(2000, "error.sharedResponsibilities.maxLength")
//  )
//)

//mandatoryIf(
//  isEqual("outsideRepairs", "both"),
//  default(text, "").verifying(
//    nonEmpty(errorMessage = "error.sharedResponsibilities.required"),
//    maxLength(2000, "error.sharedResponsibilities.maxLength"))
//)

//mandatoryIfAnyAreTrue(
//  Seq(OutsideRepairsBoth.name, InsideRepairsBoth.name,  BuildingInsuranceBoth.name),
//  default(text, "").verifying(
//    nonEmpty(errorMessage = "error.sharedResponsibilities.required"),
//    maxLength(2000, "error.sharedResponsibilities.maxLength")
//  )
//)

//fieldsToExclude = Seq(OutsideRepairsLandlord.name, OutsideRepairsTenant.name, InsideRepairsLandlord.name, InsideRepairsTenant.name, BuildingInsuranceLandlord.name, BuildingInsuranceLandlord.name)
