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
import SessionWrapper.{change as changeSession, unchanged as unchangedSession}

trait PermanentResidents:

  def withHasPermanentResidents(newValue: Boolean)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasPermanentResidents = Some(newValue))
        }
      case Some(lettingHistory) =>
        lettingHistory.hasPermanentResidents match
          case None           =>
            changeSession {
              lettingHistory.copy(hasPermanentResidents = Some(newValue))
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
                    hasPermanentResidents = Some(true)
                  )
                else
                  // reset the permanent residents list
                  lettingHistory.copy(
                    hasPermanentResidents = Some(false),
                    permanentResidents = Nil,
                    mayHaveMorePermanentResidents = None
                  )
              }

  def hasPermanentResidents(session: Session): Option[Boolean] =
    for
      lettingHistory        <- session.lettingHistory
      hasPermanentResidents <- lettingHistory.hasPermanentResidents
    yield hasPermanentResidents

  def byAddingOrUpdatingPermanentResident(newResident: ResidentDetail, maybeIndex: Option[Int] = None)(using
    session: Session
  ): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(
            hasPermanentResidents = Some(true),
            permanentResidents = List(newResident)
          )
        }
      case Some(lettingHistory) =>
        val maybeExistingIndex =
          maybeIndex.orElse {
            lettingHistory.permanentResidents.zipWithIndex
              .find((existing, _) => existing.name == newResident.name)
              .map((_, index) => index)
          }

        maybeExistingIndex match
          case None                =>
            // the given resident is actually new ... therefore append it to the list
            changeSession {
              lettingHistory
                .copy(hasPermanentResidents = Some(true))
                .copy(permanentResidents = lettingHistory.permanentResidents :+ newResident)
            }
          case Some(existingIndex) =>
            val oldResident = lettingHistory.permanentResidents(existingIndex)
            if newResident == oldResident
            then
              // the given resident detail is NOT changing the existing one
              unchangedSession
            else
              // the given resident detail are changing the existing one ... therefore patch the list
              changeSession {
                lettingHistory
                  .copy(hasPermanentResidents = Some(true))
                  .copy(permanentResidents =
                    lettingHistory.permanentResidents.patch(existingIndex, List(newResident), 1)
                  )
              }

  def byRemovingPermanentResidentAt(index: Int)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasPermanentResidents = Some(false), permanentResidents = Nil)
        }
      case Some(lettingHistory) =>
        val patchedList = lettingHistory.permanentResidents.patch(index, Nil, 1)
        changeSession {
          lettingHistory.copy(
            hasPermanentResidents = Some(patchedList.nonEmpty),
            permanentResidents = patchedList
          )
        }

  def permanentResidents(session: Session): List[ResidentDetail] =
    for
      lettingHistory     <- session.lettingHistory.toList
      permanentResidents <- lettingHistory.permanentResidents
    yield permanentResidents
