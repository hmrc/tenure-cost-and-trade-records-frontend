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

package form.aboutyouandtheproperty
import models.submissions.aboutyouandtheproperty.PropertyCurrentlyUsed
import org.scalatestplus.play._
import play.api.data.FormError

class PropertyCurrentlyUsedFormSpec extends PlaySpec {

  "PropertyCurrentlyUsedForm" must {

    "fail to bind when propertyCurrentlyUsed is empty" in {
      val data = Map("propertyCurrentlyUsed" -> "")
      val form = PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm.bind(data)
      form.hasErrors mustBe true
      form.errors must contain(FormError("propertyCurrentlyUsed", "error.propertyCurrentlyUsed.required"))
    }

    "fail to bind when propertyCurrentlyUsed contains 'other' and anotherUseDetails is empty" in {
      val data = Map(
        "propertyCurrentlyUsed[0]" -> "other",
        "anotherUseDetails"        -> ""
      )
      val form = PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm.bind(data)
      form.hasErrors mustBe true
      form.errors must contain(FormError("", "error.anotherUseDetails.required"))
    }

    "fail to bind when propertyCurrentlyUsed contains 'other' and anotherUseDetails exceeds max length" in {
      val longText = "a" * 201
      val data     = Map(
        "propertyCurrentlyUsed[0]" -> "other",
        "anotherUseDetails"        -> longText
      )
      val form     = PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm.bind(data)
      form.hasErrors mustBe true
      form.errors must contain(FormError("anotherUseDetails", "error.anotherUseDetails.maxLength"))
    }

    "bind successfully when propertyCurrentlyUsed contains 'other' and anotherUseDetails is within valid length" in {
      val data = Map(
        "propertyCurrentlyUsed[0]" -> "other",
        "anotherUseDetails"        -> "Some other use"
      )
      val form = PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm.bind(data)
      form.hasErrors mustBe false
      form.get mustEqual PropertyCurrentlyUsed(List("other"), Some("Some other use"))
    }

    "bind successfully when propertyCurrentlyUsed does not contain 'other'" in {
      val data = Map(
        "propertyCurrentlyUsed[0]" -> "residential",
        "anotherUseDetails"        -> ""
      )
      val form = PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm.bind(data)
      form.hasErrors mustBe false
      form.get mustEqual PropertyCurrentlyUsed(List("residential"), None)
    }
  }
}
