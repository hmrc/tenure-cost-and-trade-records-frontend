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

package config

import com.google.inject.Inject
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsObject, Json}

class AddressLookupConfig @Inject() (messagesApi: MessagesApi) {

  def config(continueUrl: String, feedbackUrl: String)(implicit language: Lang) = {

    val cy       = Lang("CY")
    val v2Config = s"""{
                      |  "version": 2,
                      |  "options": {
                      |    "continueUrl": "$continueUrl",
                      |    "phaseFeedbackLink": "/help/alpha",
                      |    "showPhaseBanner": false,
                      |    "alphaPhase": false,
                      |    "showBackButtons": true,
                      |    "includeHMRCBranding": true,
                      |    "ukMode": true,
                      |    "selectPageConfig": {
                      |      "proposalListLimit": 10,
                      |      "showSearchLinkAgain": true
                      |    },
                      |    "confirmPageConfig": {
                      |      "showChangeLink": true,
                      |      "showSubHeadingAndInfo": true,
                      |      "showSearchAgainLink": true,
                      |      "showConfirmChangeText": false
                      |    },
                      |    "timeoutConfig": {
                      |      "timeoutAmount": 890,
                      |      "timeoutUrl": "${controllers.routes.SaveAsDraftController.sessionTimeout.url}"
                      |    }
                      |},
                      |    "labels": {
                      |      "en": {
                      |        "appLevelLabels": {
                      |          "navTitle":  "${messagesApi("site.service_name")}"
                      |        },
                      |        "lookupPageLabels": {
                      |          "title": "What is your landlord''s address?",
                      |          "heading": "What is your landlord''s address?",
                      |          "noResultsFoundMessage" : "There are no results for that postcode",
                      |          "resultLimitExceededMessage": "Too many results - add a building name or number, or ender the address manually"
                      |        },
                      |        "selectPageLabels": {
                      |          "title": "Choose your landlord''s address",
                      |          "heading": "Choose your landlord''s address",
                      |          "proposalListLabel": "Choose your landlord''s address or enter it manually",
                      |          "editAddressLinkText": "Enter the address manually"
                      |         },
                      |        "editPageLabels": {
                      |          "title": "Enter the address manually",
                      |          "heading": "Enter the address manually"
                      |        },
                      |        "confirmPageLabels": {
                      |         "title": "Check your landlord''s address",
                      |         "heading": "Check your landlord''s address",
                      |         "submitLabel": "Use this address",
                      |         "searchAgainLinkText": "Use a different address",
                      |         "changeLinkText": "Edit this address",
                      |         "infoMessage": ""
                      |        }
                      |      },
                      |      "cy": {
                      |        "appLevelLabels": {
                      |          "navTitle":  "${messagesApi("site.service_name")(cy)}"
                      |        },
                      |        "lookupPageLabels": {
                      |          "title": "What is your landlord''s address?",
                      |          "heading": "What is your landlord''s address?",
                      |          "noResultsFoundMessage" : "There are no results for that postcode",
                      |          "resultLimitExceededMessage": "Too many results - add a building name or number, or ender the address manually"
                      |        },
                      |        "selectPageLabels": {
                      |          "title": "Choose your landlord''s address",
                      |          "heading": "Choose your landlord''s address",
                      |          "proposalListLabel": "Choose your landlord''s address or enter it manually",
                      |          "editAddressLinkText": "Enter the address manually"
                      |         },
                      |        "editPageLabels": {
                      |          "title": "Enter the address manually",
                      |          "heading": "Enter the address manually"
                      |        },
                      |        "confirmPageLabels": {
                      |         "title": "Check your landlord''s address",
                      |         "heading": "Check your landlord''s address",
                      |         "submitLabel": "Use this address",
                      |         "searchAgainLinkText": "Use a different address",
                      |         "changeLinkText": "Edit this address",
                      |         "infoMessage": ""
                      |        }
                      |      }
                      |    }
                      |  }""".stripMargin

    Json.parse(v2Config).as[JsObject]
  }
}
