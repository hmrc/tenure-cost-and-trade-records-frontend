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

package connectors

import config.{AddressLookupConfig, AppConfig}
import models.AddressLookup
import play.api.Logging
import play.api.i18n.Lang
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject() (
  appConfig: AppConfig,
  servicesConfig: ServicesConfig,
  addressLookupConfig: AddressLookupConfig,
  httpClientV2: HttpClientV2
)(implicit executionContext: ExecutionContext)
    extends Logging {

  private val serviceUrl        = servicesConfig.baseUrl("address-lookup-frontend")
  private val hostUrl           = appConfig.tctrFrontendUrl
  private val addressLookupURL  = url"$serviceUrl/api/v2/init"
  private val getAddressByIdUrl = s"$serviceUrl/api/confirmed?id="
  private val feedbackUrl       = s"$hostUrl/send-trade-and-cost-information/feedback"

  def initialise(call: Call, from: String = "")(implicit hc: HeaderCarrier, language: Lang): Future[Option[String]] = {
    val continueUrl = from match {
      case "CYA" => hostUrl + call.url + "?from=CYA"
      case "TL"  => hostUrl + call.url + "?from=TL"
      case _     => hostUrl + call.url
    }

    val addressConfig = Json.toJson(addressLookupConfig.config(continueUrl = continueUrl, feedbackUrl = feedbackUrl))

    httpClientV2
      .post(addressLookupURL)
      .withBody(addressConfig)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case 202   =>
            Some(
              response
                .header(key = "Location")
                .getOrElse(s"[AddressLookupConnector][initialise] - Failed to obtain location from $addressLookupURL")
            )
          case other =>
            logger.warn(s"[AddressLookupConnector][initialise] - received HTTP status $other from $addressLookupURL")
            None
        }
      }
      .recover { case e: Exception =>
        logger.warn(s"[AddressLookupConnector][initialise] - connection to $addressLookupURL failed", e)
        None
      }
  }

  def getAddress(id: String)(implicit hc: HeaderCarrier): Future[AddressLookup] =
    httpClientV2
      .get(url"$getAddressByIdUrl$id")
      .execute[AddressLookup]

}
