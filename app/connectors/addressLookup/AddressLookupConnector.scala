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

package connectors.addressLookup

import actions.SessionRequest
import config.AppConfig
import play.api.Logging
import play.api.i18n.Lang
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

import javax.inject.Inject
import scala.concurrent.Future.{failed, successful}
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject() (
  mcc: MessagesControllerComponents,
  appConfig: AppConfig,
  servicesConfig: ServicesConfig,
  httpClientV2: HttpClientV2
)(using ec: ExecutionContext)
    extends FrontendHeaderCarrierProvider
    with Logging:

  def initJourney(config: AddressLookupConfig)(implicit request: SessionRequest[AnyContent]): Future[Option[String]] =
    val additionalParameter =
      if request.isFromCheckYourAnswer then "from=CYA"
      else if request.isFromTaskList then "from=TL"
      else ""

    val separator =
      if additionalParameter.isEmpty
      then ""
      else if config.offRampCall.url.contains("?")
      then "&"
      else "?"

    val messages = mcc.messagesApi
    given Lang   = messages.preferred(request).lang

    // See https://github.com/hmrc/address-lookup-frontend?tab=readme-ov-file#configuring-a-journey
    val journeyConfig = Json.parse(s"""{
        |  "version": 2,
        |  "options": {
        |    "continueUrl": "${hostUrl + config.offRampCall.url + separator + additionalParameter}",
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
        |  },
        |  "labels": {
        |    "en": {
        |      "appLevelLabels": {
        |        "navTitle":  "${messages("site.service_name")}"
        |      },
        |      "lookupPageLabels": {
        |        "title": "${messages(config.lookupPageHeadingKey)}",
        |        "heading": "${messages(config.lookupPageHeadingKey)}"
        |      },
        |      "selectPageLabels": {
        |        "title": "${messages(config.selectPageHeadingKey)}",
        |        "heading": "${messages(config.selectPageHeadingKey)}"
        |       },
        |      "editPageLabels": {
        |        "title": "Enter the address manually",
        |        "heading": "Enter the address manually"
        |      },
        |      "confirmPageLabels": {
        |        "title": "${messages(config.confirmPageLabelKey)}",
        |        "heading": "${messages(config.confirmPageLabelKey)}",
        |        "submitLabel": "Use this address",
        |        "searchAgainLinkText": "Use a different address",
        |        "changeLinkText": "Edit this address",
        |        "infoMessage": ""
        |      }
        |    },
        |    "cy": {
        |      "appLevelLabels": {
        |        "navTitle":  "${messages("site.service_name")(using Lang("CY"))}"
        |      },
        |      "lookupPageLabels": {
        |        "title": "${messages(config.lookupPageHeadingKey)}",
        |        "heading": "${messages(config.lookupPageHeadingKey)}"
        |      },
        |      "selectPageLabels": {
        |        "title": "${messages(config.selectPageHeadingKey)}",
        |        "heading": "${messages(config.selectPageHeadingKey)}"
        |       },
        |      "editPageLabels": {
        |        "title": "Rhowch y cyfeiriad â llaw",
        |        "heading": "Rhowch y cyfeiriad â llaw"
        |      },
        |      "confirmPageLabels": {
        |       "title": "${messages(config.confirmPageLabelKey)}",
        |       "heading": "${messages(config.confirmPageLabelKey)}",
        |       "submitLabel": "Defnyddiwch y cyfeiriad hwn",
        |       "searchAgainLinkText": "Defnyddiwch gyfeiriad gwahanol",
        |       "changeLinkText": "Golygu'r cyfeiriad hwn",
        |       "infoMessage": ""
        |      }
        |    }
        |  }
        |}""".stripMargin)

    httpClientV2
      .post(initUrl)
      .withBody(journeyConfig)
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match
          case 202    =>
            // Extract the "on-ramp" location from the 202 response
            val onRampLocation = response.header(key = "Location")
            if onRampLocation.isEmpty then
              logger.warn(
                s"Could not receive the on-ramp location from the ADDRESS_LOOKUP_FRONTEND service (see $initUrl) despite it responded with HTTP 202"
              )
            successful(onRampLocation)
          case status =>
            logger.warn(s"Got unexpected HTTP $status response from the ADDRESS_LOOKUP_FRONTEND service (see $initUrl)")
            successful(None)
      }
      .recoverWith { case e =>
        failed(new Exception(s"Could not connect to the ADDRESS_LOOKUP_FRONTEND service (see $initUrl)", e))
      }

  def getConfirmedAddress(id: String)(implicit hc: HeaderCarrier): Future[AddressLookupConfirmedAddress] =
    httpClientV2
      .get(url"$confirmedUrl$id")
      .execute[AddressLookupConfirmedAddress]

  private val baseUrl      = servicesConfig.baseUrl("address-lookup-frontend")
  private val initUrl      = url"$baseUrl/api/v2/init"
  private val confirmedUrl = s"$baseUrl/api/confirmed?id="
  private val hostUrl      = appConfig.tctrFrontendUrl
