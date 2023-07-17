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

package models.submissions

import models.submissions.common.ContactDetails
import uk.gov.hmrc.govukfrontend.views.Aliases

/**
  * @author Yuriy Tumakha
  */
trait PrintableYourContactDetails {

  def fullName: String

  def contactDetails: ContactDetails

  def additionalInformation: Option[String]

  def yourContactDetails: List[String] =
    List(
      Some(fullName),
      Some(contactDetails.phone),
      Some(contactDetails.email),
      additionalInformation
    ).flatten
}

object PrintableYourContactDetails {
  implicit class PrintableYourContactDetailsHelper(printableYourContactDetails: PrintableYourContactDetails) extends Aliases {
    def escapedHtml: String =
      printableYourContactDetails.yourContactDetails
        .map(Text(_).asHtml)
        .mkString("""<p class="govuk-body">""", "<br/> ", "</p>")
  }
}
