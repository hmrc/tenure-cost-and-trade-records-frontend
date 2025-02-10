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

package models

import play.api.mvc.Call

enum DeclarationTaskState:
  case CannotStartYet, NotStarted, InProgress, Completed

case class DeclarationTask(
  id: String,
  messageKey: String,
  state: DeclarationTaskState,
  call: Call
):
  val isCompleted = state == DeclarationTaskState.Completed

case class DeclarationSection(
  id: String,
  messageKey: String,
  tasks: List[DeclarationTask]
):
  val isCompleted = tasks.forall(_.isCompleted)

case class FormOfReturn(
  sections: List[DeclarationSection]
):
  val isCompleted            = sections.forall(_.isCompleted)
  val size                   = sections.size
  val completedSectionsCount = sections.count(_.isCompleted)
