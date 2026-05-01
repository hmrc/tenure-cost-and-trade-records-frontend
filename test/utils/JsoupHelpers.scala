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

package utils

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer
import org.apache.pekko.util.Timeout
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.matchers.{MatchResult, Matcher}
import play.api.mvc.Result
import play.api.test.Helpers.contentAsString

import scala.concurrent.Future
import scala.jdk.CollectionConverters.*

trait JsoupHelpers:
  extension (el: Element) def value: String = Option(el.`val`()).get

  extension (d: Document)
    def heading: String  = d.select("h1").first().text().trim
    def backLink: String = d.select("a.govuk-back-link").first().attr("href")

    def errorMessage(id: String): String =
      d.select(s"""p.govuk-error-message[id="$id-error"]""").textNodes().asScala.last.text().trim
    def error(id: String): String        = d.select(s"""a[href="#$id"]""").textNodes().asScala.last.text().trim
    def checkbox(n: String): Element     = d.select(s"""input.govuk-checkboxes__input[name="$n"]""").first()
    def radios(n: String): List[Element] = d.select(s"""input.govuk-radios__input[name="$n"]""").asScala.toList
    def input(n: String): Element        = d.select(s"""input.govuk-input[name="$n"]""").first()
    def textarea(n: String): Element     = d.select(s"""textarea.govuk-textarea[name="$n"]""").first()
    def submitAction: String             = d.select("form").first().attr("action")

    def summaryList: List[String] =
      d.select("""dl.govuk-summary-list""")
        .first()
        .children()
        .asScala
        .toList
        .map { el =>
          el.select("dt.govuk-summary-list__key").first().text()
        }

  def contentAsJsoup(result: Future[Result])(using timeout: Timeout, mat: Materializer = NoMaterializer): Document =
    Jsoup.parse(contentAsString(result))

  // --------------------------------------------------------
  //  S C A L A T E S T     M A T C H E R s
  // --------------------------------------------------------

  def beChecked    = new CheckboxElementMatcher(expectedCheck = true)
  def notBeChecked = new CheckboxElementMatcher(expectedCheck = false)

  class CheckboxElementMatcher(expectedCheck: Boolean) extends Matcher[Element]:

    override def apply(actualElement: Element): MatchResult =
      MatchResult(
        matches = actualElement.hasAttr("checked") == expectedCheck,
        rawFailureMessage = s"""Checkbox was not ${if expectedCheck then "checked" else "unchecked"}""",
        rawNegatedFailureMessage = s"""Checkbox was ${if expectedCheck then "unchecked" else "checked"}"""
      )

  class RadioElementsMatcher(expectedValue: Option[String]) extends Matcher[List[Element]]:

    override def apply(actualElements: List[Element]): MatchResult =
      MatchResult(
        matches =
          if expectedValue.isEmpty
          then !actualElements.exists(_.hasAttr("check"))
          else actualElements.exists(el => el.hasAttr("checked") && el.attr("value") == expectedValue.get),
        rawFailureMessage =
          if expectedValue.isEmpty
          then """Radio group elements did not have none checked"""
          else s"""Radio group elements did not have "${expectedValue.get}" checked""",
        rawNegatedFailureMessage =
          if expectedValue.isEmpty
          then """Radio group elements had some checked"""
          else s"""Radio group elements had checked values different than "${expectedValue.get}""""
      )

  def haveNoneChecked            = new RadioElementsMatcher(expectedValue = None)
  def haveChecked(value: String) = new RadioElementsMatcher(expectedValue = Some(value))

  class InputElementMatcher(expectedValue: Option[String]) extends Matcher[Element]:

    override def apply(actualElement: Element): MatchResult =
      MatchResult(
        matches =
          if expectedValue.isEmpty
          then !actualElement.hasAttr("value")
          else actualElement.attr("value") == expectedValue.get,
        rawFailureMessage = s"Input element did not have value $expectedValue",
        rawNegatedFailureMessage = s"Input element had value $expectedValue"
      )

  def haveValue(value: String) = new InputElementMatcher(expectedValue = Some(value))
  def beEmpty                  = new InputElementMatcher(expectedValue = None)

object JsoupHelpers extends JsoupHelpers
