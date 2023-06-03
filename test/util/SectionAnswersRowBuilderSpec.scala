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

package util

import controllers.aboutyouandtheproperty
import models.submissions.aboutyouandtheproperty.{CurrentPropertyOther, PropertyDetails}
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, SummaryListRow, Value}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class SectionAnswersRowBuilderSpec extends TestBaseSpec {

  private def sectionAnswers =
    SectionAnswersRowBuilder(aboutYouAndTheProperty6010YesSession.aboutYouAndTheProperty)(messages)

  private val expectedRow = Seq(
    SummaryListRow(
      key = Key(Text("Full name")),
      value = Value(Text("Tobermory")),
      actions = Some(
        Actions(items =
          Seq(
            ActionItem(
              href = "/send-trade-and-cost-information/about-you?from=CYA#fullName",
              content = Text("Change"),
              visuallyHiddenText = Some("Full name")
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

    "return empty Set as value is None" in {
      sectionAnswers.optionalRow(
        "label.fullName",
        _ => None,
        aboutyouandtheproperty.routes.AboutYouController.show(),
        "fullName"
      ) shouldBe Seq.empty
    }

    "return answer value with conditional text field value `Other<br/>Details for other option` if selected option `Other`" in {
      val answers = SectionAnswersRowBuilder(
        aboutYouAndTheProperty6010YesSession.aboutYouAndTheProperty.map(
          _.copy(propertyDetails =
            Some(PropertyDetails("currentOccupierName", List(CurrentPropertyOther), Some("Details for other option")))
          )
        )
      )(messages)

      val row = answers.row(
        "checkYourAnswersAboutTheProperty.propertyUsage",
        _.propertyDetails.map(_.propertyCurrentlyUsed).map(usage => s"${usage.head.key}.${usage.head.name}"),
        aboutyouandtheproperty.routes.AboutThePropertyController.show(),
        "propertyCurrentlyUsed",
        ("propertyCurrentlyUsed.other", _.propertyDetails.flatMap(_.currentlyUsedOtherField))
      )

      row shouldBe Seq(
        SummaryListRow(
          key = Key(Text("How is the property currently used?")),
          value = Value(HtmlContent("Other<br/>Details for other option")),
          actions = Some(
            Actions(items =
              Seq(
                ActionItem(
                  href = "/send-trade-and-cost-information/about-the-property?from=CYA#propertyCurrentlyUsed",
                  content = Text("Change"),
                  visuallyHiddenText = Some("How is the property currently used?")
                )
              )
            )
          )
        )
      )

    }

  }

}
