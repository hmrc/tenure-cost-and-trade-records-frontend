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

package views.behaviours

import org.scalatest.matchers.must.Matchers._
import play.api.i18n.Lang
import play.twirl.api.HtmlFormat
import views.ViewSpecBase

import java.util.Locale

trait ViewBehaviours extends ViewSpecBase {

  def labelDefinedAndUsedOnce(option: String, prefix: String, view: () => HtmlFormat.Appendable) = {
    val doc   = asDocument(view())
    assert(messages.isDefinedAt(s"$prefix.$option"))
    val label = doc.select(s"label[for=$prefix.$option]")
    assert(label.size() == 1)
  }

  def normalPage(view: () => HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: String*) =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc  = asDocument(view())
          val nav  = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc
              .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
              .first()
              .parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(doc, "title", messages("service.title", messages(s"$messageKeyPrefix.heading")))
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading")
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          doc.getElementById("cymraeg-switch") != null || !doc
            .getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg")
            .isEmpty
        }
      }
    }

  def normalPageWithMessageExtra(
    view: () => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    messageExtra: String,
    expectedGuidanceKeys: String*
  ) =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc  = asDocument(view())
          val nav  = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc
              .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
              .first()
              .parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages(s"$messageKeyPrefix.heading", messageExtra))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", messageExtra)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          doc.getElementById("cymraeg-switch") != null || !doc
            .getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg")
            .isEmpty
        }
      }
    }

  def normalPageWithDifferentHeadingAndTitle(
    view: () => HtmlFormat.Appendable,
    messageKeyPrefix: String,
    expectedGuidanceKeys: String*
  ) =
    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc  = asDocument(view())
          val nav  = Option {
            doc.getElementById("proposition-menu")
          }.getOrElse(
            doc
              .getElementsByAttributeValue("class", "hmrc-header__service-name hmrc-header__service-name--linked")
              .first()
              .parent()
          )
          val span = nav.children.first
          span.text mustBe messagesApi("site.service_name")(Lang(Locale.UK))
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(
            doc,
            "title",
            messages("service.title", messages(s"$messageKeyPrefix.heading"))
          )
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.title")
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display language toggles" in {
          val doc = asDocument(view())
          doc.getElementById("cymraeg-switch") != null || !doc
            .getElementsByAttributeValue("href", "/valuation-office-agency-contact-frontend/language/cymraeg")
            .isEmpty
        }
      }
    }
}
