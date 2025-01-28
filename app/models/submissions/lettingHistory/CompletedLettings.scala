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

trait CompletedLettings:

  def withHasCompletedLettings(newValue: Boolean)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasCompletedLettings = Some(newValue))
        }
      case Some(lettingHistory) =>
        lettingHistory.hasCompletedLettings match
          case None           =>
            changeSession {
              lettingHistory.copy(hasCompletedLettings = Some(newValue))
            }
          case Some(oldValue) =>
            if newValue == oldValue
            then
              // the given value is NOT changing the old one
              unchangedSession
            else
              changeSession {
                if newValue == true
                then
                  lettingHistory.copy(
                    hasCompletedLettings = Some(true)
                  )
                else
                  // reset the completed lettings list
                  lettingHistory.copy(
                    hasCompletedLettings = Some(false),
                    completedLettings = Nil,
                    mayHaveMoreCompletedLettings = None
                  )
              }

  def hasCompletedLettings(session: Session): Option[Boolean] =
    for
      lettingHistory       <- session.lettingHistory
      hasCompletedLettings <- lettingHistory.hasCompletedLettings
    yield hasCompletedLettings

  def byAddingOrUpdatingOccupier(newOccupier: OccupierDetail, maybeIndex: Option[Int] = None)(using
    session: Session
  ): (Int, SessionWrapper) =
    session.lettingHistory match
      case None                 =>
        (
          /* index = */ 0,
          changeSession {
            LettingHistory(
              hasCompletedLettings = Some(true),
              completedLettings = List(newOccupier)
            )
          }
        )
      case Some(lettingHistory) =>
        val maybeExistingIndex =
          maybeIndex.orElse {
            lettingHistory.completedLettings.zipWithIndex
              .find((existing, _) => existing.name == newOccupier.name)
              .map((_, index) => index)
          }

        maybeExistingIndex match
          case None                =>
            // the given occupier is actually new ... therefore APPEND it to the list
            val lastIndex                 = lettingHistory.completedLettings.size
            val extendedCompletedLettings = lettingHistory.completedLettings :+ newOccupier
            (
              lastIndex,
              changeSession {
                lettingHistory
                  .copy(hasCompletedLettings = Some(true))
                  .copy(completedLettings = extendedCompletedLettings)
              }
            )
          case Some(existingIndex) =>
            val oldOccupier = lettingHistory.completedLettings(existingIndex)
            if newOccupier == oldOccupier
            then
              // the given occupier detail is NOT changing the existing one
              (existingIndex, unchangedSession)
            else
              // the given occupier detail are changing the existing one ... therefore PATCH the list
              val patchedOccupier          =
                lettingHistory
                  .completedLettings(existingIndex)
                  .copy(name = newOccupier.name, address = newOccupier.address)
              val patchedCompletedLettings =
                lettingHistory.completedLettings.patch(existingIndex, List(patchedOccupier), 1)
              (
                existingIndex,
                changeSession {
                  lettingHistory
                    .copy(hasCompletedLettings = Some(true))
                    .copy(completedLettings = patchedCompletedLettings)
                }
              )

  def byUpdatingOccupierRentalPeriod(index: Int, newRentalPeriod: LocalPeriod)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case Some(lettingHistory) =>
        lettingHistory.completedLettings.lift(index) match
          case Some(existingOccupier) =>
            existingOccupier.rentalPeriod match
              case Some(oldRentalPeriod) if newRentalPeriod == oldRentalPeriod =>
                unchangedSession
              case _                                                           =>
                val patchedOccupier          = existingOccupier.copy(rentalPeriod = Some(newRentalPeriod))
                val patchedCompletedLettings = lettingHistory.completedLettings.patch(index, List(patchedOccupier), 1)
                changeSession {
                  lettingHistory.copy(completedLettings = patchedCompletedLettings)
                }
          case None                   =>
            unchangedSession

      case None =>
        changeSession {
          LettingHistory(
            hasCompletedLettings = None,
            completedLettings = Nil
          )
        }

  def byRemovingCompletedLettingAt(index: Int)(using session: Session): SessionWrapper =
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(hasCompletedLettings = Some(false), completedLettings = Nil)
        }
      case Some(lettingHistory) =>
        val patchedList = lettingHistory.completedLettings.patch(index, Nil, 1)
        changeSession {
          lettingHistory.copy(
            hasCompletedLettings = Some(patchedList.nonEmpty),
            completedLettings = patchedList
          )
        }

  def completedLettings(session: Session): List[OccupierDetail] =
    for
      lettingHistory    <- session.lettingHistory.toList
      completedLettings <- lettingHistory.completedLettings
    yield completedLettings
