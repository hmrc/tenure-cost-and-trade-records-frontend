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

package actions

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.mvc.Results._
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, Result}
import play.api.test.Helpers._
import play.api.test.FakeRequest
import utils.TestBaseSpec

import scala.concurrent.Future

class RefNumActionSpec extends TestBaseSpec {

  "RefNumAction" should {
    val bodyParsers    = inject[BodyParsers.Default]
    val action         = new RefNumAction(bodyParsers, messagesApi)
    val sessionRequest = FakeRequest().withSession()

    "return a Redirect result when 'refNum' is not present in session" in {

      val result: Future[Result] =
        action.invokeBlock(sessionRequest, (_: RefNumRequest[AnyContentAsEmpty.type]) => Future.successful(Ok("")))

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.LoginController.show().url)
    }

    "return OK  when 'refNum' is present in session" in {

      val refNum                 = "test"
      val requestWithRefNum      = FakeRequest().withSession("refNum" -> refNum)
      val result: Future[Result] = action.invokeBlock(
        requestWithRefNum,
        (refNumRequest: RefNumRequest[AnyContentAsEmpty.type]) =>
          Future.successful(Ok(s"RefNum: ${refNumRequest.refNum}"))
      )

      status(result) mustBe OK
    }
  }
}
