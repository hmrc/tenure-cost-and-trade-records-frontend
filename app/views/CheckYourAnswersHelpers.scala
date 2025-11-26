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

package views

import play.api.mvc.Call

object CheckYourAnswersHelpers:

  extension (call: Call)
    def asChangeLink(fragment: String): Call =
      call.copy(
        url = withFrom + s"&change=true#$fragment"
      )

    def withFromCheckYourAnswer(fragment: String): Call =
      call.copy(
        url = withFrom + s";$fragment"
      )

    private def withFrom: String =
      // Append the "from" query string parameter with a "CYA" value
      call.url + (if call.url.contains("?") then "&" else "?") + "from=CYA"
