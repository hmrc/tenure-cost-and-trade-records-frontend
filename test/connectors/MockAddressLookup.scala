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

import connectors.addressLookup.*
import utils.MockitoExtendedSugar

import scala.concurrent.Future.successful

trait MockAddressLookup extends MockitoExtendedSugar:

  val addressLookupConnector = mock[AddressLookupConnector]
  when(addressLookupConnector.initJourney(any[AddressLookupConfig])(any)).thenReturn(successful(Some("/on-ramp")))

  val addressLookupConfirmedAddress = AddressLookupConfirmedAddress(
    address = AddressLookupAddress(
      lines = Some(Seq("line1", "line2", "line3")),
      postcode = Some("postcode"),
      country = None
    ),
    auditRef = "auditRef",
    id = Some("id")
  )
  when(addressLookupConnector.getConfirmedAddress(anyString)(any)).thenReturn(successful(addressLookupConfirmedAddress))
