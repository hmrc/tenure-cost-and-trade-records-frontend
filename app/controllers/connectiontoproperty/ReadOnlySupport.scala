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

package controllers.connectiontoproperty

import actions.SessionRequest
import models.submissions.common.AnswersYesNo.*
import play.api.mvc.AnyContent

/**
  * Trait to provide read-only support for connection to property controllers.
  */
trait ReadOnlySupport:

  /**
    * Determines if the current section can be locked into read-only mode based on the user's answers.
    *
    * @param request the session request containing user data.
    * @return boolean indicating if the section can be locked into read-only mode
    */
  def isReadOnly(using request: SessionRequest[AnyContent]): Boolean =
    val answerChecked =
      for
        stillConnectedDetails      <- request.sessionData.stillConnectedDetails
        checkYourAnswersAndConfirm <- stillConnectedDetails.checkYourAnswersConnectionToProperty
      yield checkYourAnswersAndConfirm.answersChecked

    answerChecked.contains(AnswerYes)
