/*
 * Copyright 2023 HM Revenue & Customs
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

import java.time.Clock
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.google.inject.name.Names.named
import com.typesafe.config.ConfigException
import play.api._
import repositories._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import scala.util.Try

class GuiceModule(
  environment: Environment,
  configuration: Configuration
) extends AbstractModule {

  lazy val servicesConfig: ServicesConfig = new ServicesConfig(configuration)

  override def configure() = {

    bind(classOf[SessionRepo])
      .annotatedWith(Names.named("session"))
      .to(classOf[SessionRepository])
    bind(classOf[Clock]).toInstance(Clock.systemUTC())

  }

  protected def bindBoolean(path: String, name: String = ""): Unit =
    bindConstant()
      .annotatedWith(named(resolveAnnotationName(path, name)))
      .to(
        Try(configuration.get[String](path).toBoolean).toOption.getOrElse(configException(path))
      ) //We need to parse as string, due to the process of adding in from app-config-<env> it is seen as a string

  protected def bindStringWithPrefix(path: String, prefix: String, name: String = ""): Unit =
    bindConstant()
      .annotatedWith(named(resolveAnnotationName(path, name)))
      .to(s"$prefix${configuration.get[String](path)}")

  private def resolveAnnotationName(path: String, name: String): String = name match {
    case "" => path
    case _  => name
  }

  private def configException(path: String) = throw new ConfigException.Missing(path)
}
