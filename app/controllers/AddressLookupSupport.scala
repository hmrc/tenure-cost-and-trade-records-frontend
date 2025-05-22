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

package controllers

import actions.SessionRequest
import connectors.addressLookup.{AddressLookupConfig, AddressLookupConfirmedAddress, AddressLookupConnector}
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future.failed
import scala.concurrent.{ExecutionContext, Future}

trait AddressLookupSupport(connector: AddressLookupConnector)(using ec: ExecutionContext):
  this: FrontendController =>

  def redirectToAddressLookupFrontend(
    config: AddressLookupConfig
  )(using request: SessionRequest[AnyContent]): Future[Result] =
    connector
      .initJourney(config)
      .flatMap {
        case Some(onRampUrl) => SeeOther(onRampUrl)
        case None            =>
          failed(
            new Exception(
              "The AddressLookupConnector did not receive the on-ramp location from the ADDRESS_LOOKUP_FRONTEND service"
            )
          )
      }

  def getConfirmedAddress(id: String)(using hc: HeaderCarrier): Future[AddressLookupConfirmedAddress] =
    connector.getConfirmedAddress(id)
