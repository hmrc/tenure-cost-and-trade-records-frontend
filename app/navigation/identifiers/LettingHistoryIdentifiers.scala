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

package navigation.identifiers

case object PermanentResidentsPageId extends Identifier:
  override def toString: String = "permanentResidentsPage"

case object ResidentDetailPageId extends Identifier:
  override def toString: String = "residentDetailPage"

case object ResidentRemovePageId extends Identifier:
  override def toString: String = "residentRemovePage"

case object ResidentListPageId extends Identifier:
  override def toString: String = "residentListPage"

case object CompletedLettingsPageId extends Identifier:
  override def toString: String = "commercialLettingsPage"

case object OccupierDetailPageId extends Identifier:
  override def toString: String = "occupierDetailPage"

extension (string: String)
  def asPageIdentifier: Option[Identifier] = string match
    case "permanentResidentsPage" => Some(PermanentResidentsPageId)
    case "residentDetailPage"     => Some(ResidentDetailPageId)
    case "residentRemovePage"     => Some(ResidentRemovePageId)
    case "residentListPage"       => Some(ResidentListPageId)
    case "completedLettingsPage"  => Some(CompletedLettingsPageId)
    case "occupierDetailPage"     => Some(OccupierDetailPageId)
    case _                        => None
