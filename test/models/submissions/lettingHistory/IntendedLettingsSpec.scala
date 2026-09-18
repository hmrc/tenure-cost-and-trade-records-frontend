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
import uk.gov.hmrc.vo.unit.test.BaseSpec

import java.time.LocalDate

class IntendedLettingsSpec extends BaseSpec:

  private val session: Session = Session(
    referenceNumber = "99996048004",
    forType = FOR6048,
    address = CommonAddress("001", Some("GORING ROAD"), "GORING-BY-SEA, WORTHING", Some("WEST SUSSEX"), "BN12 4AX"),
    token = "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    lettingHistory = None
  )

  private val date: LocalDate = LocalDate.of(2024, 3, 31)

  private val period: LocalPeriod = LocalPeriod(date, date)

  "the IntendedLettings trait" when {
    "copying the session withNumberOfNights" should {
      "set an integer value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed                               shouldBe true
        session.data.lettingHistory                  shouldNot be(None)
        intendedLettings(session.data)               shouldNot be(None)
        intendedLettings(session.data).get.nights.get shouldBe 100
      }

      "set an integer value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed                               shouldBe true
        session.data.lettingHistory                  shouldNot be(None)
        intendedLettings(session.data)               shouldNot be(None)
        intendedLettings(session.data).get.nights.get shouldBe 100
      }

      "set an integer value although lettingHistory.intendedLettings.nights was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(nights = None)
      ) {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed                               shouldBe true
        session.data.lettingHistory                  shouldNot be(None)
        intendedLettings(session.data)               shouldNot be(None)
        intendedLettings(session.data).get.nights.get shouldBe 100
      }

      "keep the value if lettingHistory.intendedLettings.nights is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(nights = Some(100))
      ) {
        val session: SessionWrapper = withNumberOfNights(100)
        session.changed                               shouldBe false
        session.data.lettingHistory                  shouldNot be(None)
        intendedLettings(session.data)               shouldNot be(None)
        intendedLettings(session.data).get.nights.get shouldBe 100
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
        val session: SessionWrapper = withNumberOfNights(141)
        session.changed                                      shouldBe true
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.nights.get        shouldBe 141
        intendedLettings(session.data).get.hasStopped        shouldBe None
        intendedLettings(session.data).get.whenWasLastLet    shouldBe None
        intendedLettings(session.data).get.isYearlyAvailable shouldBe None
        intendedLettings(session.data).get.tradingSeason     shouldBe None
      }
    }

    "copying the session withHasStopped" should {
      "set a boolean value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withHasStopped(true)
        session.changed                                   shouldBe true
        session.data.lettingHistory                      shouldNot be(None)
        intendedLettings(session.data)                   shouldNot be(None)
        intendedLettings(session.data).get.hasStopped.get shouldBe true
      }

      "set a boolean value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withHasStopped(true)
        session.changed                                   shouldBe true
        session.data.lettingHistory                      shouldNot be(None)
        intendedLettings(session.data)                   shouldNot be(None)
        intendedLettings(session.data).get.hasStopped.get shouldBe true
      }

      "set a boolean value although lettingHistory.intendedLettings.hasStopped was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(hasStopped = None)
      ) {
        val session: SessionWrapper = withHasStopped(true)
        session.changed                                   shouldBe true
        session.data.lettingHistory                      shouldNot be(None)
        intendedLettings(session.data)                   shouldNot be(None)
        intendedLettings(session.data).get.hasStopped.get shouldBe true
      }

      "keep the value if lettingHistory.intendedLettings.hasStopped is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(hasStopped = Some(true))
      ) {
        val session: SessionWrapper = withHasStopped(true)
        session.changed                                   shouldBe false
        session.data.lettingHistory                      shouldNot be(None)
        intendedLettings(session.data)                   shouldNot be(None)
        intendedLettings(session.data).get.hasStopped.get shouldBe true
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
        session.changed                                      shouldBe true
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.nights.get        shouldBe 100
        intendedLettings(session.data).get.hasStopped.get    shouldBe false
        intendedLettings(session.data).get.whenWasLastLet    shouldBe None
        intendedLettings(session.data).get.isYearlyAvailable shouldBe None
        intendedLettings(session.data).get.tradingSeason     shouldBe None
      }
    }

    "copying the session withWhenWasLastLet" should {
      "set a date value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed                                       shouldBe true
        session.data.lettingHistory                          shouldNot be(None)
        intendedLettings(session.data)                       shouldNot be(None)
        intendedLettings(session.data).get.whenWasLastLet.get shouldBe date
      }

      "set a date value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed                                       shouldBe true
        session.data.lettingHistory                          shouldNot be(None)
        intendedLettings(session.data)                       shouldNot be(None)
        intendedLettings(session.data).get.whenWasLastLet.get shouldBe date
      }

      "set a date value although lettingHistory.intendedLettings.whenWasLastLet was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(whenWasLastLet = None)
      ) {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed                                       shouldBe true
        session.data.lettingHistory                          shouldNot be(None)
        intendedLettings(session.data)                       shouldNot be(None)
        intendedLettings(session.data).get.whenWasLastLet.get shouldBe date
      }

      "keep the value if lettingHistory.intendedLettings.whenWasLastLet is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(whenWasLastLet = Some(date))
      ) {
        val session: SessionWrapper = withWhenWasLastLet(date)
        session.changed                                       shouldBe false
        session.data.lettingHistory                          shouldNot be(None)
        intendedLettings(session.data)                       shouldNot be(None)
        intendedLettings(session.data).get.whenWasLastLet.get shouldBe date
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
        val session: SessionWrapper   = withWhenWasLastLet(threeDaysAfter)
        session.changed                                       shouldBe true
        session.data.lettingHistory                          shouldNot be(None)
        intendedLettings(session.data)                       shouldNot be(None)
        intendedLettings(session.data).get.nights.get         shouldBe 100
        intendedLettings(session.data).get.hasStopped.get     shouldBe true
        intendedLettings(session.data).get.whenWasLastLet.get shouldBe threeDaysAfter
        intendedLettings(session.data).get.isYearlyAvailable  shouldBe None
        intendedLettings(session.data).get.tradingSeason      shouldBe None
      }
    }

    "copying the session withIsYearlyAvailable" should {
      "set a boolean value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed                                          shouldBe true
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe true
      }

      "set a boolean value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed                                          shouldBe true
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe true
      }

      "set a boolean value although lettingHistory.intendedLettings.isYearlyAvailable was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(isYearlyAvailable = None)
      ) {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed                                          shouldBe true
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe true
      }

      "keep the value if lettingHistory.intendedLettings.isYearlyAvailable is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(isYearlyAvailable = Some(true))
      ) {
        val session: SessionWrapper = withIsYearlyAvailable(true)
        session.changed                                          shouldBe false
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe true
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
        session.changed                                          shouldBe true
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.nights.get            shouldBe 100
        intendedLettings(session.data).get.hasStopped.get        shouldBe true
        intendedLettings(session.data).get.whenWasLastLet.get    shouldBe date
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe false
        intendedLettings(session.data).get.tradingSeason.get     shouldBe period
      }
    }

    "copying the session withTradingPeriod" should {
      "set a period value although the lettingHistory was None" in new SessionWithNoLettingHistory {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed                                      shouldBe true
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.tradingSeason.get shouldBe period
      }

      "set a period value although lettingHistory.intendedLettings was None" in new SessionWithNoIntendedLettings {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed                                      shouldBe true
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.tradingSeason.get shouldBe period
      }

      "set a period value although lettingHistory.intendedLettings.tradingPeriod was None" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(tradingSeason = None)
      ) {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed                                      shouldBe true
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.tradingSeason.get shouldBe period
      }

      "keep the value if lettingHistory.intendedLettings.tradingPeriod is the same" in new SessionWithSomeIntendedLettings(
        intendedLettings = IntendedDetail(tradingSeason = Some(period))
      ) {
        val session: SessionWrapper = withTradingPeriod(period)
        session.changed                                      shouldBe false
        session.data.lettingHistory                         shouldNot be(None)
        intendedLettings(session.data)                      shouldNot be(None)
        intendedLettings(session.data).get.tradingSeason.get shouldBe period
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
        val session: SessionWrapper       = withTradingPeriod(aDifferentPeriod)
        session.changed                                          shouldBe true
        session.data.lettingHistory                             shouldNot be(None)
        intendedLettings(session.data)                          shouldNot be(None)
        intendedLettings(session.data).get.nights.get            shouldBe 100
        intendedLettings(session.data).get.hasStopped.get        shouldBe true
        intendedLettings(session.data).get.whenWasLastLet.get    shouldBe date
        intendedLettings(session.data).get.isYearlyAvailable.get shouldBe true
        intendedLettings(session.data).get.tradingSeason.get     shouldBe aDifferentPeriod
      }
    }
  }

  trait SessionWithNoLettingHistory:
    given Session = session

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
