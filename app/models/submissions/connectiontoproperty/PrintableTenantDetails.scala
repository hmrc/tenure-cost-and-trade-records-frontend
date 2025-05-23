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

package models.submissions.connectiontoproperty

import uk.gov.hmrc.govukfrontend.views.Aliases

trait PrintableTenantDetails {

  def name: String

  def descriptionOfLetting: String

  def correspondenceAddress: Option[CorrespondenceAddress]

  def tenantDetails: List[String] =
    List(
      Some(name),
      Some(descriptionOfLetting),
      correspondenceAddress.map(_.buildingNameNumber),
      correspondenceAddress.map(_.street1).flatten,
      correspondenceAddress.map(_.town),
      correspondenceAddress.map(_.county).flatten,
      correspondenceAddress.map(_.postcode)
    ).flatten
}

object PrintableTenantDetails {
  implicit class PrintableTenantDetailsHelper(printableTenantDetails: PrintableTenantDetails) extends Aliases {
    def escapedHtml: String =
      printableTenantDetails.tenantDetails
        .map(Text(_).asHtml)
        .mkString("""<p class="govuk-body">""", "<br/> ", "</p>")
  }
}
