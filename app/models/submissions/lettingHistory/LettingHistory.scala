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

package models.submissions.lettingHistory

import models.Session
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import play.api.libs.json.{Format, Json}

case class LettingHistory(
  hasPermanentResidents: Option[Boolean] = None,
  permanentResidents: List[ResidentDetail] = Nil,
  mayHaveMorePermanentResidents: Option[Boolean] = None,
  hasCompletedLettings: Option[Boolean] = None,
  completedLettings: List[OccupierDetail] = Nil,
  mayHaveMoreCompletedLettings: Option[Boolean] = None,
  intendedLettings: Option[IntendedDetail] = None,
  hasOnlineAdvertising: Option[Boolean] = None,
  onlineAdvertising: List[AdvertisingDetail] = Nil,
  mayHaveMoreOnlineAdvertising: Option[Boolean] = None,
  checkYourAnswers: Option[AnswersYesNo] = None
)

object LettingHistory
    extends Object
    with PermanentResidents
    with CompletedLettings
    with IntendedLettings
    with OnlineAdvertising:

  val MaxNumberOfPermanentResidents = 5
  val MaxNumberOfCompletedLettings  = 5
  val MaxNumberOfAdvertisingOnline  = 5

  @deprecated
  def withoutTheAbilityToDetectChanges(ifEmpty: LettingHistory, copyFunc: LettingHistory => LettingHistory)(using
    session: Session
  ): SessionWrapper =
    SessionWrapper(
      data = session.copy(lettingHistory =
        Some(
          session.lettingHistory.fold(ifEmpty)(copyFunc)
        )
      ),
      // This is not good!
      // It's just a temporary solution as we need more time
      // to implement a proper "change detection" logic in this large Scala model
      changed = true
    )

  def withMayHaveMoreOf(kind: String, understand: Boolean)(using session: Session): SessionWrapper =
    withoutTheAbilityToDetectChanges(
      ifEmpty = LettingHistory(),
      copyFunc = { lettingHistory =>
        kind match
          case "permanentResidents" =>
            lettingHistory.copy(mayHaveMorePermanentResidents = Some(understand))
          case "temporaryOccupiers" =>
            lettingHistory.copy(mayHaveMoreCompletedLettings = Some(understand))
          case "onlineAdvertising"  =>
            lettingHistory.copy(mayHaveMoreOnlineAdvertising = Some(understand))
          case _                    =>
            lettingHistory
      }
    )

  def mayHaveMoreOf(kind: String)(using session: Session): Option[Boolean] =
    for
      lettingHistory <- session.lettingHistory
      mayHaveMore    <- {
        kind match
          case "permanentResidents" => lettingHistory.mayHaveMorePermanentResidents
          case "temporaryOccupiers" => lettingHistory.mayHaveMoreCompletedLettings
          case "advertisingDetails" => lettingHistory.mayHaveMoreOnlineAdvertising
          case _                    => None
      }
    yield mayHaveMore

  def withCheckYourAnswers(question: AnswersYesNo)(using session: Session): SessionWrapper =
    withoutTheAbilityToDetectChanges(
      ifEmpty = LettingHistory(checkYourAnswers = Option(question)),
      copyFunc = lettingHistory => lettingHistory.copy(checkYourAnswers = Option(question))
    )

  given Format[LettingHistory]                   = Json.format
  extension (answer: AnswersYesNo) def toBoolean = answer == AnswerYes
  extension (bool: Boolean) def toAnswer         = if bool then AnswerYes else AnswerNo
