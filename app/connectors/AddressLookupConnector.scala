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

package connectors

import config.{AddressLookupConfig, AppConfig}
import models.AddressLookup
import play.api.Logging
import play.api.i18n.Lang
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject() (
  appConfig: AppConfig,
  servicesConfig: ServicesConfig,
  addressLookupConfig: AddressLookupConfig,
  http: HttpClient
)(implicit executionContext: ExecutionContext)
    extends Logging {

  private val serviceUrl = servicesConfig.baseUrl("address-lookup-frontend")
  def initialise(call: Call, from: String = "")(implicit hc: HeaderCarrier, language: Lang): Future[Option[String]] = {

    lazy val hostUrl     = appConfig.tctrFrontendUrl
    lazy val continueUrl = from match {
      case "CYA" => hostUrl + call.url + "?from=CYA"
      case "TL"  => hostUrl + call.url + "?from=TL"
      case _     => hostUrl + call.url
    }

    lazy val feedbackUrl = s"$hostUrl/send-trade-and-cost-information/feedback"

    val addressLookupUrl = s"$serviceUrl/api/v2/init"
    val addressConfig    = Json.toJson(addressLookupConfig.config(continueUrl = continueUrl, feedbackUrl = feedbackUrl))
    http
      .POST[JsValue, HttpResponse](addressLookupUrl, body = addressConfig)
      .map { response =>
        response.status match {
          case 202   =>
            Some(
              response
                .header(key = "Location")
                .getOrElse(s"[AddressLookupConnector][initialise] - Failed to obtain location from $addressLookupUrl")
            )
          case other =>
            logger.warn(s"[AddressLookupConnector][initialise] - received HTTP status $other from $addressLookupUrl")
            None
        }
      }
      .recover { case e: Exception =>
        logger.warn(s"[AddressLookupConnector][initialise] - connection to $addressLookupUrl failed", e)
        None
      }
  }

  def getAddress(id: String)(implicit hc: HeaderCarrier): Future[AddressLookup] = {

    val getAddressUrl = s"$serviceUrl/api/confirmed?id=$id"
    http.GET[AddressLookup](getAddressUrl)
  }
}
