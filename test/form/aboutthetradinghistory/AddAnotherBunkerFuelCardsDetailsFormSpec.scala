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

package form.aboutthetradinghistory

import form.aboutthetradinghistory.AddAnotherBunkerFuelCardsDetailsForm._
import models.submissions.common.{AnswerNo, AnswerYes}
import org.scalatestplus.play.PlaySpec
import play.api.data.FormError

class AddAnotherBunkerFuelCardsDetailsFormSpec extends PlaySpec {

  "AddAnotherBunkerFuelCardsDetailsForm" should {

    "bind a 'yes' value correctly" in {
      val data = Map("addAnotherBunkerFuelCardsDetails" -> "yes")
      val form = baseAddAnotherBunkerFuelCardsDetailsForm.bind(data)

      form.errors mustBe empty
      form.value mustBe Some(AnswerYes)
    }

    "bind a 'no' value correctly" in {
      val data = Map("addAnotherBunkerFuelCardsDetails" -> "no")
      val form = baseAddAnotherBunkerFuelCardsDetailsForm.bind(data)

      form.errors mustBe empty
      form.value mustBe Some(AnswerNo)
    }

    "not bind an empty map" in {
      val form = baseAddAnotherBunkerFuelCardsDetailsForm.bind(Map.empty[String, String])

      form.errors mustNot be(empty)
      form.errors must contain(
        FormError("addAnotherBunkerFuelCardsDetails", "error.addAnotherBunkerFuelCardsDetails.required")
      )
    }
  }
}
