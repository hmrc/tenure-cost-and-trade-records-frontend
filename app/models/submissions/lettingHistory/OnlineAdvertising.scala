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
import models.submissions.lettingHistory.SessionWrapper.{change as changeSession, unchanged as unchangedSession}

trait OnlineAdvertising:
  @deprecated
  def withoutTheAbilityToDetectChanges(ifEmpty: LettingHistory, copyFunc: LettingHistory => LettingHistory)(using
    session: Session
  ): SessionWrapper

  def withHasOnlineAdvertising(newValue: Boolean)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasOnlineAdvertising = Some(newValue))
        }
      case Some(lettingHistory) =>
        lettingHistory.hasOnlineAdvertising match
          case None           =>
            changeSession {
              lettingHistory.copy(hasOnlineAdvertising = Some(newValue))
            }
          case Some(oldValue) =>
            if newValue == oldValue
            then
              // the given value is NOT changing the old one
              unchangedSession
            else
              // the given value is changing the old one
              changeSession {
                if newValue == true
                then
                  lettingHistory.copy(
                    hasOnlineAdvertising = Some(true)
                  )
                else
                  // reset the permanent residents list
                  lettingHistory.copy(
                    hasOnlineAdvertising = Some(false),
                    onlineAdvertising = Nil,
                    mayHaveMoreOnlineAdvertising = None
                  )
              }

  def hasOnlineAdvertising(session: Session): Option[Boolean] =
    for
      lettingHistory       <- session.lettingHistory
      hasOnlineAdvertising <- lettingHistory.hasOnlineAdvertising
    yield hasOnlineAdvertising

  @deprecated(message = "This method is not yet able to detect changes")
  def byAddingOrUpdatingOnlineAdvertising(index: Option[Int], newAdvert: AdvertisingDetail)(using
    session: Session
  ): SessionWrapper =
    // TODO Implement the ability the detect changes (see PermanentResidents.byAddingOrUpdatingPermanentResident)
    withoutTheAbilityToDetectChanges(
      ifEmpty = LettingHistory(
        hasOnlineAdvertising = Some(true),
        onlineAdvertising = List(newAdvert)
      ),
      copyFunc = lettingHistory => {
        val updatedDetails = index match {
          case Some(idx) =>
            lettingHistory.onlineAdvertising.zipWithIndex.map {
              case (_, i) if i == idx => newAdvert
              case (existingData, _)  => existingData
            }
          case None      =>
            lettingHistory.onlineAdvertising :+ newAdvert
        }
        lettingHistory.copy(
          hasOnlineAdvertising = if updatedDetails.isEmpty then Some(false) else Some(true),
          onlineAdvertising = updatedDetails
        )
      }
    )

  def byRemovingAdvertisingDetailsAt(index: Int)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasOnlineAdvertising = Some(false), onlineAdvertising = Nil)
        }
      case Some(lettingHistory) =>
        val patchedList = lettingHistory.onlineAdvertising.patch(index, Nil, 1)
        changeSession {
          lettingHistory.copy(
            hasOnlineAdvertising = Some(patchedList.nonEmpty),
            onlineAdvertising = patchedList
          )
        }

  def onlineAdvertising(session: Session): List[AdvertisingDetail] =
    for
      lettingHistory    <- session.lettingHistory.toList
      onlineAdvertising <- lettingHistory.onlineAdvertising
    yield onlineAdvertising
