/*
 * Copyright 2024 HM Revenue & Customs
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

package models

import models.audit.SavedAsDraftEvent
import play.api.libs.json.{Json, OFormat}

/**
  * @author Yuriy Tumakha
  */
case class SubmissionDraft(
  forType: ForType,
  session: Session,
  exitPath: String
) {

  def toSavedAsDraftEvent: SavedAsDraftEvent = SavedAsDraftEvent(forType, session.toUserData, exitPath)

}

object SubmissionDraft {
  implicit val format: OFormat[SubmissionDraft] = Json.format
}
