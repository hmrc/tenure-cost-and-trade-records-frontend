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

package form.aboutYourLeaseOrTenure

import form.DateMappings.requiredDateMapping
import models.submissions.aboutYourLeaseOrTenure.IntervalsOfRentReview
import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.validation.Constraints.maxLength
import play.api.i18n.Messages

object IntervalsOfRentReviewForm {

  def intervalsOfRentReviewForm(implicit messages: Messages): Form[IntervalsOfRentReview] =
    Form(
      mapping(
        "intervalsOfRentReview" -> optional(text)
          .verifying("error.intervalsOfRent.maxLength", mL => mL.forall(_.length <= 100)),
        "nextReview"            -> optional(requiredDateMapping("nextReview", allowFutureDates = true))
      )(IntervalsOfRentReview.apply)(IntervalsOfRentReview.unapply)
    )

}
