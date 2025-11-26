/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers
import org.scalatest.{AppendedClues, Assertion, Succeeded}

trait HtmlAssertionHelper { this: Matchers =>

  import AppendedClues._

  protected def assertPageContainsElement(html: Document, elementId: String): Assertion =
    Option(
      html.body().getElementById(elementId)
    ) should not be empty withClue s"Element with id $elementId was not found!"

  protected def assertPageDoesNotContainElement(html: Document, elementId: String): Assertion =
    Option(
      html.body().getElementById(elementId)
    ) shouldBe empty withClue s"Element with id $elementId was not meant to be present on the page"

  protected def assetPageContainsSummaryErrors(html: Document, expectedSummaryErrors: List[String]): Assertion = {
    val unorderedListElement = html.body().getElementsByClass("govuk-error-summary__list").first()
    withClue("Unexpected number of errors in the Errors Summary") {
      unorderedListElement.select("li").size() shouldEqual expectedSummaryErrors.length
    }

    expectedSummaryErrors.zipWithIndex
      .map { case (expectedError, i) =>
        unorderedListElement.select(s"li:nth-child(${i + 1}) > a").text() shouldEqual expectedError
      }
      .forall(_ == Succeeded) shouldEqual true
  }

}
