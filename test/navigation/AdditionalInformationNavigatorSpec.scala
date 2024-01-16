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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import models.Session
import models.submissions.aboutyouandtheproperty.ContactDetailsQuestion
import models.submissions.common.AnswerNo

import scala.concurrent.ExecutionContext

class AdditionalInformationNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "Additional information navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, additionalInformationSession)
        .apply(additionalInformationSession) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to  contact details question page when further information has been completed" in {
      navigator
        .nextPage(FurtherInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) mustBe controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController
        .show()
    }

    "return a function that goes to alternative contact details page when contact details question has been answered 'yes'" in {
      navigator
        .nextPage(ContactDetailsQuestionId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) mustBe controllers.additionalinformation.routes.AlternativeContactDetailsController
        .show()
    }

    "return a function that goes to CYA page when contact details question has been answered 'no'" in {
      val additionalInformationSessionNo: Session =
        stillConnectedDetailsYesSession.copy(additionalInformation =
          Some(prefilledAdditionalInformation.copy(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
        )

      navigator
        .nextPage(ContactDetailsQuestionId, additionalInformationSessionNo)
        .apply(
          additionalInformationSessionNo
        ) mustBe controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
    }

    "return a function that goes to task list page when CYA has been completed" in {
      navigator
        .nextPage(CheckYourAnswersAdditionalInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) mustBe controllers.routes.TaskListController
        .show()
    }
  }
}
