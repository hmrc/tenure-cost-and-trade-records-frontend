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
import models.submissions.common.AnswerNo
import play.api.libs.json.{Json, OFormat}

case class AboutLeaseOrAgreementPartThree(
  tradeServicesIndex: Int = 0,
  servicesPaidIndex: Int = 0,
  tradeServices: IndexedSeq[TradeServices] = IndexedSeq.empty,
  servicesPaid: IndexedSeq[ServicesPaid] = IndexedSeq.empty,
  throughputAffectsRent: Option[ThroughputAffectsRent] = None,
  carParking: Option[CarParking] = None,
  rentedEquipmentDetails: Option[String] = None,
  paymentForTradeServices: Option[PaymentForTradeServices] = None,
  typeOfTenure: Option[TypeOfTenure] = None, // Add March 2024 for 6020
  propertyUpdates: Option[PropertyUpdates] = None,
  leaseSurrenderedEarly: Option[LeaseSurrenderedEarly] = None,
  benefitsGiven: Option[BenefitsGiven] = None,
  benefitsGivenDetails: Option[BenefitsGivenDetails] = None,
  capitalSumDescription: Option[CapitalSumDescription] = None
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

  def updateCarParking(
    update: CarParking => CarParking
  )(implicit sessionRequest: SessionRequest[_]): Session =
    updateAboutLeaseOrAgreementPartThree { aboutLeaseOrAgreementPartThree =>
      val carParking = update(aboutLeaseOrAgreementPartThree.carParking.getOrElse(CarParking()))
      aboutLeaseOrAgreementPartThree.copy(carParking = Some(carParking))
    }

  def updateThroughputAffectsRent(
    update: ThroughputAffectsRent => ThroughputAffectsRent
  )(implicit sessionRequest: SessionRequest[_]): Session =
    updateAboutLeaseOrAgreementPartThree { aboutLeaseOrAgreementPartThree =>
      val throughputAffectsRent = update(
        aboutLeaseOrAgreementPartThree.throughputAffectsRent.getOrElse(ThroughputAffectsRent(AnswerNo))
      )
      aboutLeaseOrAgreementPartThree.copy(throughputAffectsRent = Some(throughputAffectsRent))
    }

}
