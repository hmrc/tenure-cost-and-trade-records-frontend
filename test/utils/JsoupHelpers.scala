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

package utils

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer
import org.apache.pekko.util.Timeout
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import play.api.mvc.Result
import play.api.test.Helpers.contentAsString

import scala.concurrent.Future
import scala.jdk.CollectionConverters.*

trait JsoupHelpers:
  extension (el: Element) def value = Option(el.`val`()).get
  extension (d: Document)
    def heading           = d.select("h1.govuk-heading-l").first().text().trim
    def backLink          = d.select("a.govuk-back-link").first().attribute("href").getValue
    def error(id: String) = d.select(s"""p.govuk-error-message[id="$id-error"]""").textNodes().asScala.last.text().trim
    def radio(v: String)  = d.select(s"""input.govuk-radios__input[value="$v"]""")

  def contentAsJsoup(result: Future[Result])(using timeout: Timeout, mat: Materializer = NoMaterializer): Document =
    Jsoup.parse(contentAsString(result))

object JsoupHelpers extends JsoupHelpers
