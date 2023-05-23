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

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Key, SummaryListRow}

/**
 * Rows builder for CYA section answers.
 *
  * @author Yuriy Tumakha
  */
case class SectionAnswersRowBuilder[T](answers: Option[T])(implicit messages: Messages) {

  /**
   * Render {@link uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow} with action to edit answer value.
   */
  def row(
    messageKey: String,
    getAnswerValue: T => Option[String],
    editPage: Call,
    editField: String,
    conditionalTextMapping: (String, T => Option[String])*
  ): Seq[SummaryListRow] =
    Seq(
      SummaryListRow(
        key = Key(Text(messages(messageKey))),
        value = Value(
          if (conditionalTextMapping.isEmpty) {
            Text(answers.flatMap(getAnswerValue).getOrElse(""))
          } else {
            val answerMsgKey       = answers.flatMap(getAnswerValue).getOrElse("")
            val conditionalTextMap = conditionalTextMapping.toMap
            HtmlContent(
              conditionalTextMap
                .get(answerMsgKey)
                .flatMap(answers.flatMap)
                .fold(messages(answerMsgKey))(text => s"${messages(answerMsgKey)}<br/>${Text(text).asHtml}")
            )
          }
        ),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = s"${editPage.url}?from=CYA#$editField",
                content = Text(messages("label.change")),
                visuallyHiddenText = Some(messages(messageKey))
              )
            )
          )
        )
      )
    )

  /**
   * Render {@link uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow} if `condition` returns true.
   */
  def conditionRow(
    condition: T => Boolean,
    messageKey: String,
    getAnswerValue: T => Option[String],
    editPage: Call,
    editField: String,
    conditionalTextMapping: (String, T => Option[String])*
  ): Seq[SummaryListRow] =
    if (answers.exists(condition)) {
      row(messageKey, getAnswerValue, editPage, editField, conditionalTextMapping: _*)
    } else {
      Seq.empty[SummaryListRow]
    }

  /**
   * Render {@link uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow} if answer value is defined.
   */
  def optionalRow(
    messageKey: String,
    getAnswerValue: T => Option[String],
    editPage: Call,
    editField: String,
    conditionalTextMapping: (String, T => Option[String])*
  ): Seq[SummaryListRow] =
    conditionRow(
      getAnswerValue(_).isDefined,
      messageKey,
      getAnswerValue,
      editPage,
      editField,
      conditionalTextMapping: _*
    )

}
