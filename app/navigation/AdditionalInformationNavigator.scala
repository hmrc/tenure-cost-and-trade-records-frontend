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

package navigation

import connectors.Audit
import models.Session
import navigation.identifiers._
import play.api.i18n.Lang.logger
import play.api.mvc.Call

import javax.inject.Inject

class AdditionalInformationNavigator @Inject() (audit: Audit) extends Navigator(audit) {

  override def cyaPage: Option[Call] =
    Some(controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController.show())

  private def contactDetailsQuestionRouting: Session => Call = answers => {
    answers.additionalInformation.flatMap(
      _.altDetailsQuestion.map(_.contactDetailsQuestion.name)
    ) match {
      case Some("yes") => controllers.additionalinformation.routes.AlternativeContactDetailsController.show()
      case Some("no")  => controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController.show()
      case _           =>
        logger.warn(
          s"Navigation for alternative details question reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for alternative details question routing")
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    FurtherInformationId                    -> (_ => controllers.additionalinformation.routes.ContactDetailsQuestionController.show()),
    ContactDetailsQuestionId                -> contactDetailsQuestionRouting,
    AlternativeContactDetailsId             -> (_ =>
      controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController.show()
    ),
    CheckYourAnswersAdditionalInformationId -> (_ => controllers.routes.TaskListController.show())
  )
}
