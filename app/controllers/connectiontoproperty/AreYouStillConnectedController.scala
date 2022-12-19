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

///*
// * Copyright 2022 HM Revenue & Customs
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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import form.PastConnectionForm.pastConnectionForm
import form.connectiontoproperty.AreYouStillConnectedForm.areYouStillConnectedForm
import form.connectiontoproperty.ConnectionToThePropertyForm.connectionToThePropertyForm
import form.connectiontoproperty.EditAddressForm.editAddressForm
import models.submissions.Form6010.{AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AreYouStillConnectedPageId
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.connectiontoproperty._
import models.submissions.StillConnectedDetails.updateStillConnectedDetails
import models.Session
import views.html.login
import views.html.pastConnection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
//  login: login,
  areYouStillConnectedView: areYouStillConnected,
//  pastConnectionView: pastConnection,
  connectionToThePropertyView: connectionToTheProperty,
//  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        areYouStillConnectedView(
          request.sessionData.StillConnectedDetails match {
            case Some(stillConnectedDetails) =>
              stillConnectedDetails.addressConnectionType match {
                case Some(addressConnectionType) => areYouStillConnectedForm.fillAndValidate(addressConnectionType)
                case _                           => areYouStillConnectedForm
              }
            case _                           => areYouStillConnectedForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    areYouStillConnectedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(areYouStillConnectedView(formWithErrors))),


        data => {
//                    val updatedData = request.sessionData.copy(addressConnectionType = Some(data))
                    val updatedData = updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
                    session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
          Future.successful(Redirect(navigator.nextPage(AreYouStillConnectedPageId).apply(updatedData)))
                  }


//          data => {
//
////            val updatedData: Session = request.sessionData.copy(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
////            val test1 = updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
////            val test2 = request.sessionData.copy(userLoginDetails = request.sessionData.userLoginDetails, updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
//            val updatedData: Session = updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
//            session.saveOrUpdate(updatedData)
//            navigator.nextPage(AreYouStillConnectedPageId).apply(updatedData)
//
//            if (data.equals(AddressConnectionTypeYes)) {
//              session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
//              Future.successful(Ok(connectionToThePropertyView(connectionToThePropertyForm, controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url)))
//            } else if (data.equals(AddressConnectionTypeNo)) {
//              session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
//              Future.successful(Ok(pastConnectionView(pastConnectionForm)))
//            } else if (data.equals(AddressConnectionTypeYesChangeAddress)) {
//              session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
//              Future.successful(Ok(editAddressView(editAddressForm)))
//            } else {
//              Future.successful(Ok(login(loginForm)))
//            }
//          }
      )
  }

}
