/*
 * Copyright 2026 HM Revenue & Customs
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

import models.ForType.FOR6048
import models.Session
import models.submissions.common.Address as CommonAddress
import models.submissions.lettingHistory.LettingHistory.*
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class IntendedLettingsSpec extends AnyWordSpec with Matchers with OptionValues:

  "the IntendedLettings trait" when {
    "copying the session withNumberOfNights"    should {
      "set an integer value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
      }
      "set an integer value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
      }
      "set an integer value although lettingHistory.intendedLettings.nights was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(nights = None)
      ) {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
      }
      "keep the value if lettingHistory.intendedLettings.nights is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(nights = Some(100))
      ) {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed mustBe false
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
      }
      "change the value if lettingHistory.intendedLettings.nights is different" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(
          nights = Some(100),
          hasStopped = Some(true),
          whenWasLastLet = Some(date),
          isYearlyAvailable = Some(true),
          tradingSeason = Some(period)
        )
      ) {
        val session: SessionWrapper = withNumberOfNights(141) // meets criteria
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 141
        intendedLettings(session.data).value.hasStopped mustBe None
        intendedLettings(session.data).value.whenWasLastLet mustBe None
        intendedLettings(session.data).value.isYearlyAvailable mustBe None
        intendedLettings(session.data).value.tradingSeason mustBe None
      }
    }
    "copying the session withHasStopped"        should {
      "set a boolean value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withHasStopped(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.hasStopped.value mustBe true
      }
      "set a boolean value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withHasStopped(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.hasStopped.value mustBe true
      }
      "set a boolean value although lettingHistory.intendedLettings.hasStopped was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(hasStopped = None)
      ) {
        val session: SessionWrapper = withHasStopped(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.hasStopped.value mustBe true
      }
      "keep the value if lettingHistory.intendedLettings.hasStopped is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(hasStopped = Some(true))
      ) {
        val session: SessionWrapper = withHasStopped(true)
        session.changed mustBe false
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.hasStopped.value mustBe true
      }
      "change the value if lettingHistory.intendedLettings.hasStopped is different" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(
          nights = Some(100),
          hasStopped = Some(true),
          whenWasLastLet = Some(date),
          isYearlyAvailable = Some(true),
          tradingSeason = Some(period)
        )
      ) {
        val session: SessionWrapper = withHasStopped(false)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
        intendedLettings(session.data).value.hasStopped.value mustBe false
        intendedLettings(session.data).value.whenWasLastLet mustBe None
        intendedLettings(session.data).value.isYearlyAvailable mustBe None
        intendedLettings(session.data).value.tradingSeason mustBe None
      }
    }
    "copying the session withWhenWasLastLet"    should {
      "set a date value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
      }
      "set a date value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
      }
      "set a date value although lettingHistory.intendedLettings.whenWasLastLet was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(whenWasLastLet = None)
      ) {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
      }
      "keep the value if lettingHistory.intendedLettings.whenWasLastLet is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(whenWasLastLet = Some(date))
      ) {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed mustBe false
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
      }
      "change the value if lettingHistory.intendedLettings.whenWasLastLet is different" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(
          nights = Some(100),
          hasStopped = Some(true),
          whenWasLastLet = Some(date),
          isYearlyAvailable = Some(true),
          tradingSeason = Some(period)
        )
      ) {
        val threeDaysAfter: LocalDate = date.plusDays(3)
        val session: SessionWrapper        = withWhenWasLastLet(threeDaysAfter)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
        intendedLettings(session.data).value.hasStopped.value mustBe true
        intendedLettings(session.data).value.whenWasLastLet.value mustBe threeDaysAfter
        intendedLettings(session.data).value.isYearlyAvailable mustBe None
        intendedLettings(session.data).value.tradingSeason mustBe None
      }
    }
    "copying the session withIsYearlyAvailable" should {
      "set a boolean value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe true
      }
      "set a boolean value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe true
      }
      "set a boolean value although lettingHistory.intendedLettings.isYearlyAvailable was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(isYearlyAvailable = None)
      ) {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe true
      }
      "keep the value if lettingHistory.intendedLettings.isYearlyAvailable is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(isYearlyAvailable = Some(true))
      ) {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed mustBe false
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe true
      }
      "change the value if lettingHistory.intendedLettings.isYearlyAvailable is different" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(
          nights = Some(100),
          hasStopped = Some(true),
          whenWasLastLet = Some(date),
          isYearlyAvailable = Some(true),
          tradingSeason = Some(period)
        )
      ) {
        val session: SessionWrapper = withIsYearlyAvailable(false)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
        intendedLettings(session.data).value.hasStopped.value mustBe true
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe false
        intendedLettings(session.data).value.tradingSeason.value mustBe period
      }
    }
    "copying the session withTradingPeriod"     should {
      "set a period value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.tradingSeason.value mustBe period
      }
      "set a period value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.tradingSeason.value mustBe period
      }
      "set a period value although lettingHistory.intendedLettings.tradingPeriod was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(tradingSeason = None)
      ) {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.tradingSeason.value mustBe period
      }
      "keep the value if lettingHistory.intendedLettings.tradingPeriod is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(tradingSeason = Some(period))
      ) {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed mustBe false
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.tradingSeason.value mustBe period
      }
      "change the value if lettingHistory.intendedLettings.tradingPeriod is different" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(
          nights = Some(100),
          hasStopped = Some(true),
          whenWasLastLet = Some(date),
          isYearlyAvailable = Some(true),
          tradingSeason = Some(period)
        )
      ) {
        val aDifferentPeriod: LocalPeriod = period.copy(fromDate = date.plusDays(3))
        val session: SessionWrapper          = withTradingPeriod(aDifferentPeriod)
        session.changed mustBe true
        session.data.lettingHistory mustNot be(None)
        intendedLettings(session.data) mustNot be(None)
        intendedLettings(session.data).value.nights.value mustBe 100
        intendedLettings(session.data).value.hasStopped.value mustBe true
        intendedLettings(session.data).value.whenWasLastLet.value mustBe date
        intendedLettings(session.data).value.isYearlyAvailable.value mustBe true
        intendedLettings(session.data).value.tradingSeason.value mustBe aDifferentPeriod
      }
    }
  }

  val session: Session = Session(
    referenceNumber = "99996010004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  val date: LocalDate = LocalDate.of(2024, 3, 31)

  val period: LocalPeriod = LocalPeriod(date, date)

  trait SessionWithNoLettingHistory:
    given Session = session // having lettingHistory = None

  trait SessionWithNoIntendedLettings:

    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          intendedLettings = None
        )
      )
    )

  trait SessionWithSomeIntendedLettings(intendedLettings: IntendedDetail):

    given Session = session.copy(
      lettingHistory = Some(
        LettingHistory(
          intendedLettings = Some(intendedLettings)
        )
      )
    )
