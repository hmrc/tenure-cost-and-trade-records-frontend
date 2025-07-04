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

package controllers.aboutYourLeaseOrTenure

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.WhatIsYourCurrentRentBasedOnForm.whatIsYourCurrentRentBasedOnForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne.updateAboutLeaseOrAgreementPartOne
import models.submissions.aboutYourLeaseOrTenure.CurrentRentBasedOn.*
import models.submissions.aboutYourLeaseOrTenure.WhatIsYourCurrentRentBasedOnDetails
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.WhatRentBasedOnPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.whatIsYourRentBasedOn

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhatIsYourRentBasedOnController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  whatIsYourRentBasedOnView: whatIsYourRentBasedOn,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("WhatIsYourRentBasedOn")

    Future.successful(
      Ok(
        whatIsYourRentBasedOnView(
          request.sessionData.aboutLeaseOrAgreementPartOne.flatMap(_.whatIsYourCurrentRentBasedOnDetails) match {
            case Some(whatIsYourCurrentRentBasedOnDetails) =>
              whatIsYourCurrentRentBasedOnForm.fill(whatIsYourCurrentRentBasedOnDetails)
            case None                                      => whatIsYourCurrentRentBasedOnForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[WhatIsYourCurrentRentBasedOnDetails](
      whatIsYourCurrentRentBasedOnForm,
      formWithErrors => BadRequest(whatIsYourRentBasedOnView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val currentRentBasedOnValue = request.body.asFormUrlEncoded.get("currentRentBasedOn").headOption.getOrElse("")
        val isOther                 = currentRentBasedOnValue == CurrentRentBasedOnOther.toString
        if (isOther && data.describe.isEmpty) {
          val formWithCustomError = whatIsYourCurrentRentBasedOnForm
            .fillAndValidate(data)
            .withError("whatIsYourRentBasedOn", "error.whatIsYourRentBasedOn.required")
          BadRequest(whatIsYourRentBasedOnView(formWithCustomError, request.sessionData.toSummary))
        } else {
          val updatedData = updateAboutLeaseOrAgreementPartOne(_.copy(whatIsYourCurrentRentBasedOnDetails = Some(data)))
          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(WhatRentBasedOnPageId, updatedData).apply(updatedData)))

        }
      }
    )
  }
}
