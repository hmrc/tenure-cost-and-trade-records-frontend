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

package models.submissions.aboutYourLeaseOrTenure

import actions.SessionRequest
import models.Session
import play.api.libs.json.{Json, OFormat}

case class AboutLeaseOrAgreementPartThree(
  tradeServicesIndex: Int = 0,
  servicesPaidIndex: Int = 0,
  tradeServices: IndexedSeq[TradeServices] = IndexedSeq.empty,
  servicesPaid: IndexedSeq[ServicesPaid] = IndexedSeq.empty,
  paymentForTradeServices: Option[PaymentForTradeServices] = None,
  typeOfTenure: Option[TypeOfTenure] = None // Add March 2024 for 6020
)

object AboutLeaseOrAgreementPartThree {
  implicit val format: OFormat[AboutLeaseOrAgreementPartThree] = Json.format[AboutLeaseOrAgreementPartThree]

  def updateAboutLeaseOrAgreementPartThree(
    copy: AboutLeaseOrAgreementPartThree => AboutLeaseOrAgreementPartThree
  )(implicit sessionRequest: SessionRequest[_]): Session = {
    val currentAboutLeaseOrAgreementPartThree = sessionRequest.sessionData.aboutLeaseOrAgreementPartThree

    val updatedAboutLeaseOrAgreementPartThree = currentAboutLeaseOrAgreementPartThree match {
      case Some(_) => sessionRequest.sessionData.aboutLeaseOrAgreementPartThree.map(copy)
      case _       => Some(copy(AboutLeaseOrAgreementPartThree()))
    }

    sessionRequest.sessionData.copy(aboutLeaseOrAgreementPartThree = updatedAboutLeaseOrAgreementPartThree)

  }
}
