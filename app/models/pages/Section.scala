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

package models.pages

/**
  * @author Yuriy Tumakha
  */
enum Section:

  def messageKey: String = "label.section." + this

  case connectionToTheProperty, // Connection to the property
    aboutTheProperty, // About you and the property
    lettingHistory, // Letting history
    accommodation, // Accommodation details
    aboutYourTradingHistory, // Trading history
    aboutFranchisesOrLettings, // Franchises or lettings
    aboutConcessionsOrLettings, // Concessions or lettings
    aboutConcessionsFranchisesOrLettings, // Concessions, franchises or lettings
    aboutLettings, // Lettings
    aboutYourLeaseOrTenure, // Your lease or agreement
    additionalInformation // Additional information

end Section
