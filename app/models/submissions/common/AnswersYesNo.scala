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

package models.submissions.common

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum AnswersYesNo(answer: String):
  override def toString: String = answer

  def toBoolean: Boolean = this == AnswerYes

  case AnswerYes extends AnswersYesNo("yes")
  case AnswerNo extends AnswersYesNo("no")
end AnswersYesNo

object AnswersYesNo:

  implicit val format: Format[AnswersYesNo] = Scala3EnumJsonFormat.format

  def apply(answerYes: Boolean): AnswersYesNo = if (answerYes) AnswerYes else AnswerNo

  extension (boolean: Boolean) def toAnswer: AnswersYesNo = AnswersYesNo(boolean)
