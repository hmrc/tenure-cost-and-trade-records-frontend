/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import repositories.{SessionRepo, SessionRepository}
import uk.gov.hmrc.vo.service.config.VOServiceConfig

import java.time.Clock

class TCTRModule extends Module:

  private def authTokenInitialiserBinding(configuration: Configuration): Binding[InternalAuthTokenInitialiser] =
    val isCreateInternalAuthTokenOnStart = configuration.getOptional[Boolean]("create-internal-auth-token-on-start").getOrElse(false)

    if isCreateInternalAuthTokenOnStart then
      bind[InternalAuthTokenInitialiser].to[InternalAuthTokenInitialiserImpl].eagerly()
    else
      bind[InternalAuthTokenInitialiser].to[NoOpInternalAuthTokenInitialiser].eagerly()

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[?]] = Seq(
    authTokenInitialiserBinding(configuration),
    bind[VOServiceConfig].to[AppConfig],
    bind[SessionRepo].qualifiedWith("session").to[SessionRepository],
    bind[Clock].toInstance(Clock.systemUTC())
  )
