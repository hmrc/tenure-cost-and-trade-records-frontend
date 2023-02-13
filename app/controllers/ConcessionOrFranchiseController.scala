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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package controllers
//
//import actions.WithSessionRefiner
//import form.ConcessionOrFranchiseForm
//import models.Session
//import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
//import navigation.AboutThePropertyNavigator
//import navigation.identifiers.TiedForGoodsPageId
//import play.api.Logging
//import play.api.i18n.I18nSupport
//import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
//import repositories.SessionRepo
//import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
//import views.html.abouttheproperty.tiedForGoods
//
//import javax.inject.{Inject, Named, Singleton}
//import scala.concurrent.Future
//
//@Singleton
//class ConcessionOrFranchiseController @Inject()(
//  mcc: MessagesControllerComponents,
//  navigator: AboutThePropertyNavigator,
//  tiedForGoodsView: tiedForGoods,
//  withSessionRefiner: WithSessionRefiner,
//  @Named("session") val session: SessionRepo
//) extends FrontendController(mcc)
//    with I18nSupport
//    with Logging {
//
//  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
//    Future.successful(
//      Ok(
//        tiedForGoodsView(
//          request.sessionData.aboutTheProperty.flatMap(_.tiedForGoods) match {
//            case Some(tiedForGoods) => tiedForGoodsForm.fillAndValidate(tiedForGoods)
//            case _                  => tiedForGoodsForm
//          },
//          getBackLink(request.sessionData)
//        )
//      )
//    )
//  }
//
//  def submit = (Action andThen withSessionRefiner).async { implicit request =>
//    tiedForGoodsForm
//      .bindFromRequest()
//      .fold(
//        formWithErrors =>
//          Future.successful(BadRequest(tiedForGoodsView(formWithErrors, getBackLink(request.sessionData)))),
//        data => {
//          val updatedData = updateAboutTheProperty(_.copy(tiedForGoods = Some(data)))
//          session.saveOrUpdate(updatedData)
//          Future.successful(Redirect(navigator.nextPage(TiedForGoodsPageId).apply(updatedData)))
//        }
//      )
//  }
//
//  private def getBackLink(answers: Session): String =
//    answers.aboutTheProperty.flatMap(_.enforcementAction.map(_.name)) match {
//      case Some("yes") => controllers.abouttheproperty.routes.EnforcementActionBeenTakenDetailsController.show().url
//      case Some("no")  => controllers.abouttheproperty.routes.EnforcementActionBeenTakenController.show().url
//      case _           =>
//        logger.warn(s"Back link for tied goods page reached with unknown enforcement taken value")
//        controllers.routes.TaskListController.show().url
//    }
//}
