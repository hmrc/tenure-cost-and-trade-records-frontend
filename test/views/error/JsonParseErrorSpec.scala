/*
 * Copyright 2024 HM Revenue & Customs
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

package views.error

import views.behaviours.ViewBehaviours

class JsonParseErrorSpec extends ViewBehaviours{
  def createView = () => jsonErrorView(Some("backlinktologinpage"))(fakeRequest,messages)

  "JsonErrorView" must {
    "contain text " in {
      val doc = asDocument(createView())
      assert(doc.toString.contains(messages("error.json.page.heading")))
      assert(doc.toString.contains(messages("error.json.page.p1")))
      assert(doc.toString.contains(messages("error.json.page.start")))
    }
  }
}
