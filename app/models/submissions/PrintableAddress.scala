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

import uk.gov.hmrc.govukfrontend.views.Aliases

/**
  * @author Yuriy Tumakha
  */
trait PrintableAddress {

  def buildingNameNumber: String

  def street1: Option[String]

  def town: String

  def county: Option[String]

  def postcode: String

  def addressLines: List[String] =
    List(
      Some(buildingNameNumber),
      street1,
      Some(town),
      county,
      Some(postcode.replaceAll("^(\\S+?)\\s*?(\\d\\w\\w)$", "$1 $2"))
    ).flatten

  def singleLine: String = addressLines.mkString(", ")

  def multiLine: String = addressLines.mkString("<br/> ")

}

object PrintableAddress {

  implicit class PrintableAddressHelper(printableAddress: PrintableAddress) extends Aliases {

    def escapedHtml: String =
      printableAddress.addressLines
        .map(Text(_).asHtml)
        .mkString("""<p class="govuk-body">""", "<br/> ", "</p>")

  }

}
