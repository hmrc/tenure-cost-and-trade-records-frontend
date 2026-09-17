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

package test

import play.api.Configuration
import play.api.i18n.*
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.vo.unit.test.BaseSpec
import util.DateUtilLocalised

import java.util.Locale
import scala.language.implicitConversions

/**
  * @author Yuriy Tumakha
  */
class MessagesApiSpec extends BaseSpec:

  private val languages: Langs = DefaultLangs(Seq(Lang(Locale.UK), Lang(Locale.of("cy"))))

  given messagesApi: MessagesApi = DefaultMessagesApi(langs = languages)

  def messagesForLocale(locale: Locale): Messages = messagesApi.preferred(Seq(Lang(locale)))

  given Messages = messagesForLocale(Locale.UK)

  given dateUtilLocalised: DateUtilLocalised = DateUtilLocalised(LanguageUtils(languages, Configuration.empty))
