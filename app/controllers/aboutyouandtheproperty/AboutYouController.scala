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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.AboutYouForm.theForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.CustomerDetails
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.AboutYouPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.aboutYou as AboutYouView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AboutYouController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  theView: AboutYouView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AboutYou")
    val freshForm  = theForm
    val filledForm =
      for
        aboutYouAndTheProperty <- request.sessionData.aboutYouAndTheProperty
        customerDetails        <- aboutYouAndTheProperty.customerDetails
      yield theForm.fill(customerDetails)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CustomerDetails](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(formWithErrors, request.sessionData.toSummary, isReadOnly)
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(customerDetails = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(AboutYouPageId, updatedData).apply(updatedData)))
      }
    )
  }
