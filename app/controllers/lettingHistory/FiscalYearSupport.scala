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

package controllers.lettingHistory

import play.api.i18n.Messages
import util.DateUtilLocalised

import java.time.LocalDate

trait FiscalYearSupport:

  def previousFiscalYearEnd: Int =
    val now = LocalDate.now()
    if now.getMonth.getValue > 3
    then now.getYear
    else now.getYear - 1

  def currentFiscalYearEnd =
    val now = LocalDate.now()
    if now.getMonth.getValue > 3
    then now.getYear + 2
    else now.getYear + 1

  def startDateEnglish(using messages: Messages, dateUtil: DateUtilLocalised): String =
    dateUtil.formatDate(LocalDate.of(previousFiscalYearEnd - 1, 4, 1))

  def startDateWales(using messages: Messages, dateUtil: DateUtilLocalised): String =
    dateUtil.formatDate(LocalDate.of(previousFiscalYearEnd - 3, 4, 1))

  def endDate(using messages: Messages, dateUtil: DateUtilLocalised): String =
    dateUtil.formatDate(LocalDate.of(previousFiscalYearEnd, 3, 31))
