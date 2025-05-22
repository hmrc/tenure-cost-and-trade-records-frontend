/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.aboutfranchisesorlettings

import actions.SessionRequest
import models.submissions.aboutfranchisesorlettings.{Concession6015IncomeRecord, ConcessionIncomeRecord, FranchiseIncomeRecord, IncomeRecord, LettingIncomeRecord}

trait FranchiseAndLettingSupport {

  def getIncomeRecord(index: Int)(implicit request: SessionRequest[?]): Option[IncomeRecord] =
    for {
      allRecords <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      record     <- allRecords.lift(index)
    } yield record

  def getOperatorName(index: Int)(implicit request: SessionRequest[?]): String =
    getIncomeRecord(index)
      .collect {
        case franchise: FranchiseIncomeRecord       => franchise.businessDetails.fold("")(_.operatorName)
        case letting: LettingIncomeRecord           => letting.operatorDetails.fold("")(_.operatorName)
        case concession: Concession6015IncomeRecord => concession.businessDetails.fold("")(_.operatorName)
        case concession: ConcessionIncomeRecord     => concession.businessDetails.fold("")(_.operatorName)
      }
      .getOrElse("")
}
