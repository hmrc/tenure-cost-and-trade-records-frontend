/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.Form6010

import actions.WithSessionRefiner
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{aboutTheProperty, aboutThePropertyOther, websiteForProperty}
import form.Form6010.AboutThePropertyForm.aboutThePropertyForm
import form.Form6010.AboutThePropertyOtherForm.aboutThePropertyOtherForm
import form.Form6010.WebsiteForPropertyForm.websiteForPropertyForm
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import models.submissions.SectionTwo._
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  websiteForPropertyView: websiteForProperty,
  aboutThePropertyOtherView: aboutThePropertyOther,
  aboutThePropertyView: aboutTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutThePropertyView(
          request.sessionData.sectionTwo match {
            case Some(sectionTwo) =>
              sectionTwo.propertyDetails match {
                case Some(propertyDetails) => aboutThePropertyForm.fillAndValidate(propertyDetails)
                case _                     => aboutThePropertyForm
              }
            case _                => aboutThePropertyForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    aboutThePropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutThePropertyView(formWithErrors))),
        data => {
          session.saveOrUpdate(updateSectionTwo(_.copy(propertyDetails = Some(data))))
          Future.successful(Ok(websiteForPropertyView(websiteForPropertyForm)))
        }
      )
  }

}
