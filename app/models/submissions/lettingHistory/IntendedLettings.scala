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
import java.time.LocalDate

trait IntendedLettings:

  def intendedLettings(session: Session): Option[IntendedDetail] =
    for
      lettingHistory   <- session.lettingHistory
      intendedLettings <- lettingHistory.intendedLettings
    yield intendedLettings

  def withNumberOfNights(newNights: Int)(using session: Session): SessionWrapper =
    val newIntendedLettings = IntendedDetail(
      nights = Some(newNights)
    )
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(intendedLettings = Some(newIntendedLettings))
        }
      case Some(lettingHistory) =>
        lettingHistory.intendedLettings match
          case None                   =>
            changeSession {
              lettingHistory.copy(intendedLettings = Some(newIntendedLettings))
            }
          case Some(intendedLettings) =>
            intendedLettings.nights match
              case None            =>
                changeSession {
                  lettingHistory.copy(intendedLettings =
                    Some(
                      newIntendedLettings
                        .copy(nights = Some(newNights))
                    )
                  )
                }
              case Some(oldNights) =>
                if newNights == oldNights
                then unchangedSession
                else
                  val meetsCriteria = IntendedLettings.isAboveThreshold(newNights, session.isWelsh)
                  changeSession {
                    lettingHistory.copy(intendedLettings =
                      Some(
                        intendedLettings
                          .copy(nights = Some(newNights))
                          .copy(hasStopped = if meetsCriteria then None else intendedLettings.hasStopped)
                          .copy(whenWasLastLet = if meetsCriteria then None else intendedLettings.whenWasLastLet)
                          .copy(isYearlyAvailable = None)
                          .copy(tradingSeason = None)
                      )
                    )
                  }

  def intendedLettingsNights(session: Session): Option[Int] =
    for
      intendedLettings <- intendedLettings(session)
      nights           <- intendedLettings.nights
    yield nights

  def withHasStopped(newHasStopped: Boolean)(using session: Session): SessionWrapper =
    val newIntendedLettings = IntendedDetail(
      hasStopped = Some(newHasStopped)
    )
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(intendedLettings = Some(newIntendedLettings))
        }
      case Some(lettingHistory) =>
        lettingHistory.intendedLettings match
          case None                   =>
            changeSession {
              lettingHistory.copy(intendedLettings = Some(newIntendedLettings))
            }
          case Some(intendedLettings) =>
            intendedLettings.hasStopped match
              case None                =>
                changeSession {
                  lettingHistory.copy(intendedLettings =
                    Some(
                      intendedLettings
                        .copy(hasStopped = Some(newHasStopped))
                    )
                  )
                }
              case Some(oldHasStopped) =>
                if newHasStopped == oldHasStopped
                then unchangedSession
                else
                  changeSession {
                    lettingHistory.copy(intendedLettings =
                      Some(
                        intendedLettings
                          .copy(hasStopped = Some(newHasStopped))
                          .copy(whenWasLastLet = if newHasStopped then intendedLettings.whenWasLastLet else None)
                          .copy(isYearlyAvailable = None)
                          .copy(tradingSeason = None)
                      )
                    )
                  }

  def intendedLettingsHasStopped(session: Session): Option[Boolean] =
    for
      intendedLettings <- intendedLettings(session)
      hasStopped       <- intendedLettings.hasStopped
    yield hasStopped

  def withWhenWasLastLet(newWhenWasLastLet: LocalDate)(using session: Session): SessionWrapper =
    val newIntendedLettings = IntendedDetail(
      whenWasLastLet = Some(newWhenWasLastLet)
    )
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(intendedLettings = Some(newIntendedLettings))
        }
      case Some(lettingHistory) =>
        lettingHistory.intendedLettings match
          case None                   =>
            changeSession {
              lettingHistory.copy(intendedLettings = Some(newIntendedLettings))
            }
          case Some(intendedLettings) =>
            intendedLettings.whenWasLastLet match
              case None                    =>
                changeSession {
                  lettingHistory.copy(intendedLettings =
                    Some(
                      intendedLettings
                        .copy(whenWasLastLet = Some(newWhenWasLastLet))
                    )
                  )
                }
              case Some(oldWhenWasLastLet) =>
                if newWhenWasLastLet == oldWhenWasLastLet
                then unchangedSession
                else
                  changeSession {
                    lettingHistory.copy(intendedLettings =
                      Some(
                        intendedLettings
                          .copy(whenWasLastLet = Some(newWhenWasLastLet))
                          .copy(isYearlyAvailable = None)
                          .copy(tradingSeason = None)
                      )
                    )
                  }

  def intendedLettingsWhenWasLastLet(session: Session): Option[LocalDate] =
    for
      intendedLettings <- intendedLettings(session)
      whenWasLastLet   <- intendedLettings.whenWasLastLet
    yield whenWasLastLet

  def withIsYearlyAvailable(newIsYearlyAvailable: Boolean)(using session: Session): SessionWrapper =
    val newIntendedLettings = IntendedDetail(
      isYearlyAvailable = Some(newIsYearlyAvailable)
    )
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(intendedLettings = Some(newIntendedLettings))
        }
      case Some(lettingHistory) =>
        lettingHistory.intendedLettings match
          case None                   =>
            changeSession {
              lettingHistory.copy(intendedLettings = Some(newIntendedLettings))
            }
          case Some(intendedLettings) =>
            intendedLettings.isYearlyAvailable match
              case None                       =>
                changeSession {
                  lettingHistory.copy(intendedLettings =
                    Some(
                      intendedLettings
                        .copy(isYearlyAvailable = Some(newIsYearlyAvailable))
                    )
                  )
                }
              case Some(oldIsYearlyAvailable) =>
                if newIsYearlyAvailable == oldIsYearlyAvailable
                then unchangedSession
                else
                  changeSession {
                    lettingHistory.copy(intendedLettings =
                      Some(
                        intendedLettings
                          .copy(isYearlyAvailable = Some(newIsYearlyAvailable))
                          .copy(tradingSeason = if newIsYearlyAvailable then None else intendedLettings.tradingSeason)
                      )
                    )
                  }

  def intendedLettingsIsYearlyAvailable(session: Session): Option[Boolean] =
    for
      intendedLettings  <- intendedLettings(session)
      isYearlyAvailable <- intendedLettings.isYearlyAvailable
    yield isYearlyAvailable

  def withTradingPeriod(newTradingPeriod: LocalPeriod)(using session: Session): SessionWrapper =
    val newIntendedLettings = IntendedDetail(
      tradingSeason = Some(newTradingPeriod)
    )
    session.lettingHistory match
      case None                 =>
        changeSession {
          LettingHistory(intendedLettings = Some(newIntendedLettings))
        }
      case Some(lettingHistory) =>
        lettingHistory.intendedLettings match
          case None                   =>
            changeSession {
              lettingHistory.copy(intendedLettings = Some(newIntendedLettings))
            }
          case Some(intendedLettings) =>
            intendedLettings.tradingSeason match
              case None                   =>
                changeSession {
                  lettingHistory.copy(intendedLettings =
                    Some(
                      intendedLettings
                        .copy(tradingSeason = Some(newTradingPeriod))
                    )
                  )
                }
              case Some(oldTradingPeriod) =>
                if newTradingPeriod == oldTradingPeriod
                then unchangedSession
                else
                  changeSession {
                    lettingHistory.copy(intendedLettings =
                      Some(
                        intendedLettings
                          .copy(tradingSeason = Some(newTradingPeriod))
                      )
                    )
                  }

  def intendedLettingsTradingPeriod(session: Session): Option[LocalPeriod] =
    for
      intendedLettings <- intendedLettings(session)
      tradingPeriod    <- intendedLettings.tradingSeason
    yield tradingPeriod

object IntendedLettings:
  def doesMeetLettingCriteria(session: Session): Option[Boolean] =
    for
      lettingHistory   <- session.lettingHistory
      intendedLettings <- lettingHistory.intendedLettings
      nights           <- intendedLettings.nights
    yield isAboveThreshold(nights, session.isWelsh)

  private def isAboveThreshold(nights: Int, isWelsh: Boolean): Boolean =
    if isWelsh then nights >= 252 else nights >= 140
