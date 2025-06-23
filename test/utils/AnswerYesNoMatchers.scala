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

package utils

import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import org.scalatest.matchers.{MatchResult, Matcher}

trait AnswerYesNoMatchers:

  def beAnswerYes = new AnswerYesNoMatcher(expected = AnswerYes)
  def beAnswerNo  = new AnswerYesNoMatcher(expected = AnswerNo)

  class AnswerYesNoMatcher(expected: AnswersYesNo) extends Matcher[AnswersYesNo]:
    override def apply(actual: AnswersYesNo): MatchResult =
      MatchResult(
        actual == expected,
        s"""Answer $actual was not $expected"""",
        s"""Answer $actual was $expected""""
      )

object AnswerYesNoMatchers extends AnswerYesNoMatchers
