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

package controllers

import form.CustomUserPasswordForm.customUserPasswordForm
import play.api.i18n.Messages
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.DateUtil
import views.html.customPasswordSaveAsDraft

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class SaveAsDraftController @Inject() (
  cc: MessagesControllerComponents,
  customPasswordSaveAsDraftView: customPasswordSaveAsDraft,
  dateUtil: DateUtil
) extends FrontendController(cc) {

  private val saveForDays = 90

  def customPassword(exitPath: String) = Action { implicit request =>
    Ok(customPasswordSaveAsDraftView(customUserPasswordForm, expiryDate, exitPath))
  }

  def saveAsDraft(exitPath: String) = Action { implicit request =>
    customUserPasswordForm
      .bindFromRequest()
      .fold(
        formWithErrors => Ok(customPasswordSaveAsDraftView(formWithErrors, expiryDate, exitPath)),
        validData => NotImplemented
      )
  }

  private def expiryDate(implicit messages: Messages) = dateUtil formatDate LocalDate.now.plusDays(saveForDays)

}
