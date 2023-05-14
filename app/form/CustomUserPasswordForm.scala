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

package form

import models.submissions.CustomUserPassword
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.data.validation.Constraints.{minLength, pattern}

/**
  * @author Yuriy Tumakha
  */
object CustomUserPasswordForm {

  private val passwordMinLength = 8

  val customUserPasswordForm: Form[CustomUserPassword] = Form(
    mapping(
      "password"        -> text
        .verifying(
          minLength(passwordMinLength, "error.password.minLength"),
          pattern(".*\\d.*".r, error = "error.password.atLeastOneNumber"),
          pattern(".*[a-zA-Z]+.*".r, error = "error.password.atLeastOneLetter")
        ),
      "confirmPassword" -> text
    )(CustomUserPassword.apply)(CustomUserPassword.unapply)
      .verifying("saveAsDraft.error.passwordsDontMatch", data => data.confirmPassword == data.password)
  )

}
