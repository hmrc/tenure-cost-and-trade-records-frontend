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

package models.submissions.lettingHistory

import models.Session
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import SessionWrapper.{change as changeSession, unchanged as unchangedSession}
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
  sectionCompleted: Option[Boolean] = None
)

type Entry = ResidentDetail | OccupierDetail | AdvertisingDetail

object LettingHistory
    extends Object
    with PermanentResidents
    with CompletedLettings
    with IntendedLettings
    with OnlineAdvertising:

  val MaxNumberOfPermanentResidents = 5
  val MaxNumberOfCompletedLettings  = 5
  val MaxNumberOfOnlineAdvertising  = 5

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
          case "completedLettings"  =>
            lettingHistory.copy(mayHaveMoreCompletedLettings = Some(understand))
          case "onlineAdvertising"  =>
            lettingHistory.copy(mayHaveMoreOnlineAdvertising = Some(understand))
          case _                    =>
            lettingHistory
      }
    )

  def mayHaveMoreEntitiesOf(kind: String, session: Session): Option[Boolean] =
    for
      lettingHistory <- session.lettingHistory
      mayHaveMore    <- {
        kind match
          case "permanentResidents" => lettingHistory.mayHaveMorePermanentResidents
          case "completedLettings"  => lettingHistory.mayHaveMoreCompletedLettings
          case "onlineAdvertising"  => lettingHistory.mayHaveMoreOnlineAdvertising
          case _                    => None
      }
    yield mayHaveMore

  def hasEntitiesOf(kind: String, session: Session): Option[Boolean] =
    for
      lettingHistory <- session.lettingHistory
      hasEntries     <- {
        kind match
          case "permanentResidents" => lettingHistory.hasPermanentResidents
          case "completedLettings"  => lettingHistory.hasCompletedLettings
          case "onlineAdvertising"  => lettingHistory.hasOnlineAdvertising
          case _                    => None
      }
    yield hasEntries

  def countEntitiesOf(kind: String, session: Session): Option[Int] =
    for
      lettingHistory <- session.lettingHistory
      count          <- {
        kind match
          case "permanentResidents" => Some(lettingHistory.permanentResidents.size)
          case "completedLettings"  => Some(lettingHistory.completedLettings.size)
          case "onlineAdvertising"  => Some(lettingHistory.onlineAdvertising.size)
          case _                    => Some(0)
      }
    yield count

  def hasBeenAlreadyEntered(entry: Entry, at: Option[Int])(using session: Session): Boolean =
    val entries = entry match
      case _: ResidentDetail    => permanentResidents(session)
      case _: OccupierDetail    => completedLettings(session)
      case _: AdvertisingDetail => onlineAdvertising(session)

    at.fold(entries)(entries.patch(_, Nil, 1)).contains(entry)

  def withSectionCompleted(newValue: Boolean)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(sectionCompleted = Some(newValue))
        }
      case Some(lettingHistory) =>
        lettingHistory.sectionCompleted match
          case None           =>
            changeSession {
              lettingHistory.copy(sectionCompleted = Some(newValue))
            }
          case Some(oldValue) =>
            if newValue == oldValue
            then unchangedSession
            else
              changeSession {
                lettingHistory.copy(sectionCompleted = Some(newValue))
              }

  def sectionCompleted(session: Session): Option[Boolean] =
    for
      lettingHistory   <- session.lettingHistory
      sectionCompleted <- lettingHistory.sectionCompleted
    yield sectionCompleted

  given Format[LettingHistory] = Json.format
