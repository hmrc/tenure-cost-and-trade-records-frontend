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
                      |      "proposalListLimit": 100,
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
                      |          "navTitle": "${messagesApi("site.service_name")}"
                      |        },
                      |        "lookupPageLabels": {
                      |          "title": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.lookupPage.title"
    )}",
                      |          "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.lookupPage.heading"
    )}"
                      |        },
                      |        "editPageLabels": {
                      |          "title": "${messagesApi("aboutYourLeaseOrTenure.landlord.addressLookup.editPage.title")}",
                      |          "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.editPage.heading"
    )}"
                      |        },
                      |        "confirmPageLabels": {
                      |         "title": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.title"
    )}",
                      |         "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.heading"
    )}",
                      |         "submitLabel": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.submitLabel"
    )}",
                      |         "searchAgainLinkText": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.searchAgainLinkText"
    )}",
                      |         "changeLinkText": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.changeLinkText"
    )}"
                      |        }
                      |      },
                      |      "cy": {
                      |        "appLevelLabels": {
                      |          "navTitle": "${messagesApi("site.service_name")(cy)}"
                      |        },
                      |        "lookupPageLabels": {
                      |          "title": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.lookupPage.title"
    )(cy)}",
                      |          "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.lookupPage.heading"
    )(cy)}"
                      |        },
                      |        "editPageLabels": {
                      |          "title": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.editPage.title"
    )(cy)}",
                      |          "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.editPage.heading"
    )(cy)}"
                      |        },
                      |        "confirmPageLabels": {
                      |         "title": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.title"
    )(cy)}",
                      |         "heading": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.heading"
    )(cy)}",
                      |         "submitLabel": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.submitLabel"
    )(cy)}",
                      |         "searchAgainLinkText": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.searchAgainLinkText"
    )(cy)}",
                      |         "changeLinkText": "${messagesApi(
      "aboutYourLeaseOrTenure.landlord.addressLookup.confirmPageLabels.changeLinkText"
    )(cy)}"
                      |        }
                      |      }
                      |    }
                      |  }""".stripMargin

    Json.parse(v2Config).as[JsObject]
  }
}
