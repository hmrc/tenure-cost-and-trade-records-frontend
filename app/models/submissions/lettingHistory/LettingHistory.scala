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
  intendedLettings: Option[IntendedLettings] = None,
  advertisingOnline: Option[Boolean] = None,
  advertisingOnlineDetails: List[AdvertisingOnline] = Nil
)

object LettingHistory:

  val MaxNumberOfPermanentResidents = 5
  val MaxNumberOfCompletedLettings  = 5
  val MaxNumberOfAdvertisingOnline  = 5

  private def foldLettingHistory(ifEmpty: LettingHistory, copyFunc: LettingHistory => LettingHistory)(using
    session: Session
  ): Session =
    session.copy(lettingHistory =
      Some(
        session.lettingHistory.fold(ifEmpty)(copyFunc)
      )
    )

  def withPermanentResidents(hasPermanentResidents: Boolean)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(hasPermanentResidents)
      ),
      copyFunc = lettingHistory =>
        if hasPermanentResidents
        then lettingHistory.copy(hasPermanentResidents = Some(true))
        else
          lettingHistory.copy(
            hasPermanentResidents = Some(false),
            permanentResidents = Nil,
            mayHaveMorePermanentResidents = None
          )
    )

  def hasPermanentResidents(session: Session): Option[Boolean] =
    for
      lettingHistory        <- session.lettingHistory
      hasPermanentResidents <- lettingHistory.hasPermanentResidents
    yield hasPermanentResidents

  def byAddingPermanentResident(residentDetail: ResidentDetail)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(true),
        permanentResidents = List(residentDetail)
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.permanentResidents.zipWithIndex
          .find { (existingResident, _) =>
            // We may want to find the eventually existing resident by name
            existingResident.name == residentDetail.name
          }
          .map { (_, foundIndex) =>
            // patch the resident detail if found as existing
            lettingHistory
              .copy(hasPermanentResidents = Some(true))
              .copy(permanentResidents = lettingHistory.permanentResidents.patch(foundIndex, List(residentDetail), 1))
          }
          .getOrElse {
            // not found ... then append it
            lettingHistory
              .copy(hasPermanentResidents = Some(true))
              .copy(permanentResidents = lettingHistory.permanentResidents.:+(residentDetail))
          }
      }
    )

  def byRemovingPermanentResidentAt(index: Int)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(false),
        permanentResidents = Nil
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.copy(
          permanentResidents = lettingHistory.permanentResidents.patch(index, Nil, 1)
        )
      }
    )

  def permanentResidents(session: Session): List[ResidentDetail] =
    for
      lettingHistory     <- session.lettingHistory.toList
      permanentResidents <- lettingHistory.permanentResidents
    yield permanentResidents

  def hasCompletedLettings(session: Session): Option[Boolean] =
    for
      lettingHistory       <- session.lettingHistory
      hasCompletedLettings <- lettingHistory.hasCompletedLettings
    yield hasCompletedLettings

  def withCompletedLettings(hasCompletedLettings: Boolean)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasCompletedLettings = Some(hasCompletedLettings)
      ),
      copyFunc = lettingHistory =>
        if hasCompletedLettings
        then lettingHistory.copy(hasCompletedLettings = Some(true))
        else
          lettingHistory.copy(
            hasCompletedLettings = Some(false),
            completedLettings = Nil,
            mayHaveMoreCompletedLettings = None
          )
    )

  def byAddingOccupierNameAndAddress(name: String, address: Address)(using session: Session): (Int, Session) =
    val occupierDetail                 = OccupierDetail(name, address, rental = None)
    val ifEmpty: (Int, LettingHistory) =
      (
        /* index = */ 0,
        LettingHistory(
          hasCompletedLettings = Some(true),
          completedLettings = List(occupierDetail)
        )
      )

    val copyFunc: LettingHistory => (Int, LettingHistory) = { lettingHistory =>
      lettingHistory.completedLettings.zipWithIndex
        .find { (existingOccupier, _) =>
          // We may want to find the eventually existing occupier by name
          existingOccupier.name == name
        }
        .map { (_, foundIndex) =>
          // patch the occupier detail if found as existing
          val patchedOccupier = lettingHistory
            .completedLettings(foundIndex)
            .copy(name = name, address = address)

          val patchedCompletedLettings = lettingHistory.completedLettings.patch(foundIndex, List(patchedOccupier), 1)
          val copiedLettingHistory     =
            lettingHistory.copy(hasCompletedLettings = Some(true)).copy(completedLettings = patchedCompletedLettings)
          (foundIndex, copiedLettingHistory)
        }
        .getOrElse {
          // not found ... then append it to the list of completed lettings
          val lastIndex                 = lettingHistory.completedLettings.size
          val extendedCompletedLettings = lettingHistory.completedLettings.:+(occupierDetail)
          val copiedLettingHistory      =
            lettingHistory.copy(hasCompletedLettings = Some(true)).copy(completedLettings = extendedCompletedLettings)
          (lastIndex, copiedLettingHistory)
        }
    }
    val (updatedIndex, updatedSession)                    = session.lettingHistory.fold(ifEmpty)(copyFunc)
    (updatedIndex, session.copy(lettingHistory = Some(updatedSession)))

  def byAddingOccupierRentalPeriod(index: Int, rentalPeriod: LocalPeriod)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasCompletedLettings = Some(true),
        completedLettings = Nil
      ),
      copyFunc = lettingHistory =>
        val maybeUpdatedOccupierDetails =
          for occupierDetail <- lettingHistory.completedLettings.lift(index)
          yield occupierDetail.copy(rental = Some(rentalPeriod))

        val patchedCompletedLettings =
          lettingHistory.completedLettings.patch(index, maybeUpdatedOccupierDetails.toList, 1)

        lettingHistory.copy(completedLettings = patchedCompletedLettings)
    )

  def byRemovingPermanentOccupierAt(index: Int)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasCompletedLettings = Some(false),
        completedLettings = Nil
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.copy(completedLettings = lettingHistory.completedLettings.patch(index, Nil, 1))
      }
    )

  def completedLettings(session: Session): List[OccupierDetail] =
    for
      lettingHistory    <- session.lettingHistory.toList
      completedLettings <- lettingHistory.completedLettings
    yield completedLettings

  def withMayHaveMoreOf(kind: String, understand: Boolean)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(),
      copyFunc = { lettingHistory =>
        kind match {
          case "permanentResidents" =>
            lettingHistory.copy(mayHaveMorePermanentResidents = Some(understand))
          case "temporaryOccupiers" =>
            lettingHistory.copy(mayHaveMoreCompletedLettings = Some(understand))
          case _                    =>
            lettingHistory
        }
      }
    )

  def mayHaveMorePermanentResidents(session: Session): Option[Boolean] =
    for
      lettingHistory <- session.lettingHistory
      hasEvenMore    <- lettingHistory.mayHaveMorePermanentResidents
    yield hasEvenMore

  def mayHaveMoreCompletedLettings(session: Session): Option[Boolean] =
    for
      lettingHistory <- session.lettingHistory
      hasEvenMore    <- lettingHistory.mayHaveMoreCompletedLettings
    yield hasEvenMore

  def intendedLettings(session: Session): Option[IntendedLettings] =
    for
      lettingHistory   <- session.lettingHistory
      intendedLettings <- lettingHistory.intendedLettings
    yield intendedLettings

  def withNumberOfNights(nights: Int)(using session: Session): Session =
    val someIntendedLettings = Some(
      IntendedLettings(
        nights = Some(nights)
      )
    )
    foldLettingHistory(
      ifEmpty = LettingHistory(
        intendedLettings = someIntendedLettings
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.copy(intendedLettings =
          lettingHistory.intendedLettings.fold(
            ifEmpty = someIntendedLettings
          ) { intendedLettings =>
            val meetsCriteria = isAboveThreshold(nights, session.isWelsh)
            Some(
              intendedLettings
                .copy(nights = Some(nights))
                .copy(hasStopped = if meetsCriteria then None else intendedLettings.hasStopped)
            )
          }
        )
      }
    )

  def doesMeetLettingCriteria(session: Session): Option[Boolean] =
    for
      lettingHistory   <- session.lettingHistory
      intendedLettings <- lettingHistory.intendedLettings
      nights           <- intendedLettings.nights
    yield isAboveThreshold(nights, session.isWelsh)

  private def isAboveThreshold(nights: Int, isWelsh: Boolean): Boolean =
    if isWelsh then nights >= 252 else nights >= 140

  def withHasStopped(hasStopped: Boolean)(using session: Session): Session =
    val someIntendedLettings = Some(
      IntendedLettings(
        hasStopped = Some(hasStopped)
      )
    )
    foldLettingHistory(
      ifEmpty = LettingHistory(
        intendedLettings = someIntendedLettings
      ),
      copyFunc = lettingHistory =>
        lettingHistory.copy(
          intendedLettings = lettingHistory.intendedLettings.fold(
            ifEmpty = someIntendedLettings
          ) { intendedLettings =>
            Some(
              intendedLettings
                .copy(hasStopped = Some(hasStopped))
            )
          }
        )
    )

  def hasOnlineAdvertising(session: Session): Option[Boolean] =
    for
      lettingHistory       <- session.lettingHistory
      hasOnlineAdvertising <- lettingHistory.advertisingOnline
    yield hasOnlineAdvertising

  def advertisingOnlineDetails(session: Session): List[AdvertisingOnline] =
    for
      lettingHistory           <- session.lettingHistory.toList
      advertisingOnlineDetails <- lettingHistory.advertisingOnlineDetails
    yield advertisingOnlineDetails

  def withAdvertisingOnline(question: Boolean)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(advertisingOnline = Option(question)),
      copyFunc = lettingHistory => lettingHistory.copy(advertisingOnline = Option(question))
    )

  def byAddingAdvertisingOnlineDetails(advertisingOnline: AdvertisingOnline)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        advertisingOnline = Some(true),
        advertisingOnlineDetails = List(advertisingOnline)
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.advertisingOnlineDetails.zipWithIndex
          .find { (existingDetails, _) =>
            existingDetails.websiteAddress == advertisingOnline.websiteAddress
          }
          .map { (_, foundIndex) =>
            lettingHistory
              .copy(advertisingOnlineDetails =
                lettingHistory.advertisingOnlineDetails.patch(foundIndex, List(advertisingOnline), 1)
              )
          }
          .getOrElse {
            lettingHistory
              .copy(advertisingOnlineDetails = lettingHistory.advertisingOnlineDetails.:+(advertisingOnline))
          }
      }
    )

  given Format[LettingHistory]                   = Json.format
  extension (answer: AnswersYesNo) def toBoolean = answer == AnswerYes
  extension (bool: Boolean) def toAnswer         = if bool then AnswerYes else AnswerNo
