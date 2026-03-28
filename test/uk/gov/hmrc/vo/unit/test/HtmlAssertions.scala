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

package uk.gov.hmrc.vo.unit.test

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.scalatest.AppendedClues.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, Succeeded}

import scala.language.implicitConversions

/**
  * HTML assertions implemented using Jsoup and ScalaTest.
  *
  * @author Yuriy Tumakha
  */
trait HtmlAssertions:

  this: Matchers =>

  private def getElementById(elementId: String)(using html: Document): Option[Element] =
    Option(html.body().getElementById(elementId))

  private def getElementsByClass(className: String)(using html: Document): Elements =
    html.body().getElementsByClass(className)

  implicit def parsePage(html: String): Document = Jsoup.parse(html)

  def assertPageContainsElement(html: String, elementId: String): Assertion =
    assertPageContainsElement(elementId)(using html)

  def assertPageDoesNotContainElement(html: String, elementId: String): Assertion =
    assertPageDoesNotContainElement(elementId)(using html)

  def assetPageContainsSummaryErrors(html: String, expectedErrors: List[String]): Assertion =
    assetPageContainsSummaryErrors(expectedErrors)(using html)

  def assertPageContainsElement(elementId: String)(using html: Document): Assertion =
    getElementById(elementId) should not be empty withClue s"The element with id '$elementId' could not be found."

  def assertPageDoesNotContainElement(elementId: String)(using html: Document): Assertion =
    getElementById(elementId) shouldBe empty withClue s"The element with id '$elementId' was not expected to be present on the page."

  def assetPageContainsSummaryErrors(expectedErrors: List[String])(using html: Document): Assertion =
    val errorsSummary = getElementsByClass("govuk-error-summary__list").first()
    withClue("An unexpected number of errors was detected in the Errors Summary.") {
      errorsSummary.select("li").size() shouldEqual expectedErrors.length
    }
    expectedErrors.zipWithIndex
      .map { case (expectedError, i) =>
        errorsSummary.select(s"li:nth-child(${i + 1}) > a").text() shouldEqual expectedError
      }
      .forall(_ == Succeeded) shouldEqual true
