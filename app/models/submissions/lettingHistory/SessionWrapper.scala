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

package models.submissions.lettingHistory

import models.Session

case class SessionWrapper(data: Session, changed: Boolean):
  def notChanged = !changed

object SessionWrapper:
  def unchanged(using session: Session) =
    SessionWrapper(data = session, changed = false)

  def change(newLettingHistory: LettingHistory)(using session: Session): SessionWrapper =
    SessionWrapper(
      data = session.copy(lettingHistory = Some(newLettingHistory)),
      changed = true
    )
