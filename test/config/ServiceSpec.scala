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

import config.Service.convertToString
import play.api.Configuration
import utils.TestBaseSpec

class ServiceSpec extends TestBaseSpec {

  "Service" should {
    "be loaded correctly from configuration" in {
      val config                = inject[Configuration]
      val internalAuth: Service = config.get[Service]("microservice.services.internal-auth")
      internalAuth.host             shouldBe "localhost"
      internalAuth.port             shouldBe "8470"
      internalAuth.protocol         shouldBe "http"
      internalAuth.baseUrl          shouldBe "http://localhost:8470"
      internalAuth.toString         shouldBe "http://localhost:8470"
      convertToString(internalAuth) shouldBe "http://localhost:8470"
    }
  }

}
