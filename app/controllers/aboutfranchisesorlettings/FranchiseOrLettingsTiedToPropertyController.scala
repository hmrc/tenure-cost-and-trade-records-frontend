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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import form.aboutfranchisesorlettings.FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.FranchiseOrLettingsTiedToPropertyId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutfranchisesorlettings.franchiseOrLettingsTiedToProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class FranchiseOrLettingsTiedToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutFranchisesOrLettingsNavigator,
  franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        franchiseOrLettingsTiedToPropertyView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match {
            case Some(franchisesOrLettingsTiedToProperty) =>
              franchiseOrLettingsTiedToPropertyForm.fillAndValidate(franchisesOrLettingsTiedToProperty)
            case _                                        => franchiseOrLettingsTiedToPropertyForm
          },
          request.sessionData.stillConnectedDetails.flatMap(_.connectionToProperty).toString
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    franchiseOrLettingsTiedToPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              franchiseOrLettingsTiedToPropertyView(formWithErrors, request.sessionData.userLoginDetails.forNumber)
            )
          ),
        data => {
          val updatedData = updateAboutFranchisesOrLettings(_.copy(franchisesOrLettingsTiedToProperty = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(FranchiseOrLettingsTiedToPropertyId).apply(updatedData)))
        }
      )
  }

}
