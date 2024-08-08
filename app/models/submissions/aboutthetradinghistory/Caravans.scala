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

package models.submissions.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.Caravans.CaravanLettingType.{OwnedByOperator, SubletByOperator}
import models.submissions.aboutthetradinghistory.Caravans.CaravanUnitType.{Single, Twin}
import models.submissions.common.AnswersYesNo
import navigation.identifiers.*
import play.api.libs.json.{Json, OFormat}

/**
  * 6045/6046 Trading history - Static holiday or leisure caravans pages.
  *
  * @author Yuriy Tumakha
  */
case class Caravans(
  anyStaticLeisureCaravansOnSite: Option[AnswersYesNo] = None,
  openAllYear: Option[AnswersYesNo] = None,
  weeksPerYear: Option[Int] = None,
  singleCaravansAge: Option[CaravansAge] = None,
  twinUnitCaravansAge: Option[CaravansAge] = None,
  totalSiteCapacity: Option[CaravansTotalSiteCapacity] = None
)

object Caravans {
  implicit val format: OFormat[Caravans] = Json.format

  enum CaravanUnitType(unitType: String):
    override def toString: String = unitType

    case Single extends CaravanUnitType("single")
    case Twin extends CaravanUnitType("twin")
  end CaravanUnitType

  enum CaravanLettingType(lettingType: String):
    override def toString: String = lettingType

    case OwnedByOperator extends CaravanLettingType("ownedByOperator")
    case SubletByOperator extends CaravanLettingType("subletByOperator")
  end CaravanLettingType

  enum CaravanHireType(hireType: String):
    override def toString: String = hireType

    case FleetHire extends CaravanHireType("fleetHire")
    case PrivateSublet extends CaravanHireType("privateSublet")
  end CaravanHireType

  enum CaravansTradingPage(val pageId: Identifier, val unitType: CaravanUnitType, val lettingType: CaravanLettingType):
    case SingleCaravansOwnedByOperator
        extends CaravansTradingPage(SingleCaravansOwnedByOperatorId, Single, OwnedByOperator)
    case SingleCaravansSublet extends CaravansTradingPage(SingleCaravansSubletId, Single, SubletByOperator)
    case TwinCaravansOwnedByOperator extends CaravansTradingPage(TwinCaravansOwnedByOperatorId, Twin, OwnedByOperator)
    case TwinCaravansSublet extends CaravansTradingPage(TwinCaravansSubletId, Twin, SubletByOperator)
  end CaravansTradingPage

}
