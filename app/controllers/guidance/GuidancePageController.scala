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

package controllers.guidance

import models.ForType
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.guidance.guidancePage as GuidancePageView

import javax.inject.{Inject, Singleton}

@Singleton
class GuidancePageController @Inject() (
  mcc: MessagesControllerComponents,
  guidancePageView: GuidancePageView
) extends FrontendController(mcc):

  def show(forType: String): Action[AnyContent] = Action { implicit request =>
    ForType
      .find(forType)
      .fold(NotFound(s"Invalid forType: $forType")) { tpe =>
        Ok(guidancePageView(tpe, request.session.get("referenceNumber")))
      }
  }
