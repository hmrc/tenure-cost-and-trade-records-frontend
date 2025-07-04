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

import navigation.UrlHelpers.urlPlusParamPrefix
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Actions, Card, CardTitle, Key, SummaryListRow}

/**
  * Rows builder for CYA section answers.
  *
  * @author Yuriy Tumakha
  */
case class SectionAnswersRowBuilder[T](answers: Option[T])(using messages: Messages):

  /**
    * Create [[uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.Card Card]] with title and Remove link for each item in SummaryList.
    */
  def summaryListCard(itemTitle: String, removeItemLink: String): Option[Card] =
    Some(
      Card(
        title = Some(CardTitle(Text(itemTitle))),
        actions = Some(
          Actions(
            items = Seq(
              ActionItem(
                href = removeItemLink,
                content = Text(messages("label.remove")),
                attributes = Map(
                  "aria-label" -> s"${messages("label.remove")} $itemTitle"
                )
              )
            ),
            classes = "govuk-!-font-weight-regular"
          )
        )
      )
    )

  /**
    * Render [[uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow]] with action to edit answer value.
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
          if conditionalTextMapping.isEmpty then Text(answers.flatMap(getAnswerValue).getOrElse(""))
          else if conditionalTextMapping.head._1 == "valueAsHtml" then
            HtmlContent(answers.flatMap(getAnswerValue).getOrElse(""))
          else
            val answerMsgKey       = answers.flatMap(getAnswerValue).getOrElse("")
            val conditionalTextMap = conditionalTextMapping.toMap
            HtmlContent(
              conditionalTextMap
                .get(answerMsgKey)
                .flatMap(answers.flatMap)
                .fold(messages(answerMsgKey))(text => s"${messages(answerMsgKey)}<br/>${Text(text).asHtml}")
            )
        ),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = s"${urlPlusParamPrefix(editPage.url)}from=CYA&change=true${editFieldTag(editField)}",
                content = Text(messages("label.change")),
                visuallyHiddenText = Some(messages(messageKey)),
                attributes = Map(
                  "aria-label" -> s"${messages("label.change")} ${messages(messageKey)}"
                )
              )
            )
          )
        )
      )
    )

  /**
    * Render [[uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow]] without actions.
    */
  def rowWithoutActions(
    messageKey: String,
    getAnswerValue: T => Option[String]
  ): Seq[SummaryListRow] =
    Seq(
      SummaryListRow(
        key = Key(Text(messages(messageKey))),
        value = Value(HtmlContent(answers.flatMap(getAnswerValue).getOrElse("")))
      )
    )

  /**
    * Render [[uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow]] if `condition` returns true.
    */
  def conditionRow(
    condition: T => Boolean,
    messageKey: String,
    getAnswerValue: T => Option[String],
    editPage: Call,
    editField: String,
    conditionalTextMapping: (String, T => Option[String])*
  ): Seq[SummaryListRow] =
    if answers.exists(condition) then row(messageKey, getAnswerValue, editPage, editField, conditionalTextMapping*)
    else Seq.empty[SummaryListRow]

  /**
    * Render [[uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow SummaryListRow]] if answer value is defined.
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
      conditionalTextMapping*
    )

  def conditionalOptionalRow(
    condition: T => Boolean,
    messageKey: String,
    getAnswerValue: T => Option[String],
    editPage: Call,
    editField: String,
    conditionalTextMapping: (String, T => Option[String])*
  ): Seq[SummaryListRow] =
    if answers.exists(condition) then
      optionalRow(messageKey, getAnswerValue, editPage, editField, conditionalTextMapping*)
    else Seq.empty[SummaryListRow]

  def nonCYArow(
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
          if conditionalTextMapping.isEmpty then Text(answers.flatMap(getAnswerValue).getOrElse(""))
          else if conditionalTextMapping.head._1 == "valueAsHtml" then
            HtmlContent(answers.flatMap(getAnswerValue).getOrElse(""))
          else
            val answerMsgKey       = answers.flatMap(getAnswerValue).getOrElse("")
            val conditionalTextMap = conditionalTextMapping.toMap
            HtmlContent(
              conditionalTextMap
                .get(answerMsgKey)
                .flatMap(answers.flatMap)
                .fold(messages(answerMsgKey))(text => s"${messages(answerMsgKey)}<br/>${Text(text).asHtml}")
            )
        ),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = s"${urlPlusParamPrefix(editPage.url)}${editFieldTag(editField)}",
                content = Text(messages("label.change")),
                visuallyHiddenText = Some(messages(messageKey)),
                attributes = Map(
                  "aria-label" -> s"${messages("label.change")} ${messages(messageKey)}"
                )
              )
            )
          )
        )
      )
    )

  def displayLabelsForYesAnswers(labelAnswerMap: Map[String, String]): String =
    labelAnswerMap.toSeq
      .flatMap(t => Option.when(t._2 == "yes")(messages(t._1)))
      .mkString("""<p class="govuk-body">""", """</p> <p class="govuk-body">""", "</p>")

  private def editFieldTag(editFieldId: String): String =
    Option.when(editFieldId.nonEmpty)(editFieldId).fold("")(fieldId => s"#$fieldId")
