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

package navigation.identifiers

// Are you connected to the property page identifiers
case object SignInPageId extends Identifier {
  override def toString: String = "signInPage"
}

case object AreYouStillConnectedPageId extends Identifier {
  override def toString: String = "areYouStillConnectedPage"
}

case object EditAddressPageId extends Identifier {
  override def toString: String = "editAddressPage"
}

case object ConnectionToPropertyPageId extends Identifier {
  override def toString: String = "ConnectionToPropertyPage"
}

case object TaskListPageId extends Identifier {
  override def toString: String = "taskListPage"
}
