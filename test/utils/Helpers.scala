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

package utils

import actions.RefNumAction
import play.api.mvc._
import play.api.test.FakeRequest
import scala.language.implicitConversions

object Helpers {

  implicit def fakeRequest2MessageRequest[A](fakeRequest: FakeRequest[A]): MessagesRequest[A] =
    new MessagesRequest[A](fakeRequest, play.api.test.Helpers.stubMessagesApi())

  def refNumAction(): RefNumAction = {
    val cc = play.api.test.Helpers.stubControllerComponents()

    new RefNumAction(new play.api.mvc.BodyParsers.Default(cc.parsers), cc.messagesApi)(using cc.executionContext)
  }

}

import play.api.Configuration
import play.api.Environment
import play.api.Mode
import crypto.MongoCrypto

trait SensitiveTestHelper {

  class TestMongoCrypto(configuration: Configuration) extends MongoCrypto(configuration) {
    // override protected val encryptionKey: String = configuration.get[String]("crypto.key")
  }

  def loadTestConfig(): Configuration = {
    val testEnv: Environment             = Environment.simple(mode = Mode.Test)
    val devSettings: Map[String, AnyRef] = Map(
      "crypto.key" -> "P5xsJ9Nt+quxGZzB4DeLfw=="
    )
    Configuration.load(testEnv, devSettings)
  }

  def createTestMongoCrypto(configuration: Configuration): MongoCrypto =
    new TestMongoCrypto(configuration)
}
