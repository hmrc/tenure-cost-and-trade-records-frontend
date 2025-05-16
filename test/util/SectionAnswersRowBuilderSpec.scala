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

package util

import controllers.aboutyouandtheproperty
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow, Value}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class SectionAnswersRowBuilderSpec extends TestBaseSpec {

  private def sectionAnswers =
    SectionAnswersRowBuilder(aboutYouAndTheProperty6010YesSession.aboutYouAndTheProperty)(using messages)

  private val expectedRow = Seq(
    SummaryListRow(
      key = Key(Text("Full name")),
      value = Value(Text("Tobermory")),
      actions = Some(
        Actions(items =
          Seq(
            ActionItem(
              href = "/send-trade-and-cost-information/about-you?from=CYA&change=true#fullName",
              content = Text("Change"),
              visuallyHiddenText = Some("Full name"),
              attributes = Map(
                "aria-label" -> "Change Full name"
              )
            )
          )
        )
      )
    )
  )

  "SectionAnswer" should {
    "build SummaryListRow" in {
      sectionAnswers.row(
        "label.fullName",
        _.customerDetails.map(_.fullName),
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe expectedRow
    }

    "build optional SummaryListRow as value is set" in {
      sectionAnswers.optionalRow(
        "label.fullName",
        _.customerDetails.map(_.fullName),
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe expectedRow
    }

    "return conditional-optional SummaryListRow if conditional value is true " in {
      sectionAnswers.conditionalOptionalRow(
        _.customerDetails.exists(_.fullName == "Tobermory"),
        "label.fullName",
        _.customerDetails.map(_.fullName),
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe expectedRow
    }

    "return empty Set for conditional-optional SummaryListRow if conditional value is false " in {
      sectionAnswers.conditionalOptionalRow(
        _.customerDetails.exists(_.fullName == ""),
        "label.fullName",
        _.customerDetails.map(_.fullName),
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe Seq.empty
    }

    "return empty Set as value is None" in {
      sectionAnswers.optionalRow(
        "label.fullName",
        _ => None,
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe Seq.empty
    }

  }

}
