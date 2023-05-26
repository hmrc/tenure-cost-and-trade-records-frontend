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

package controllers

import actions.WithSessionRefiner
import config.ErrorHandler
import play.api.http.Status
import play.api.test.Helpers._
import stub.StubSessionRepo
import utils.TestBaseSpec
import views.html.taskList

class TaskListSpec extends TestBaseSpec {

  private val sessionRepo = StubSessionRepo()

  private def taskListController = new TaskListController(
    stubMessagesControllerComponents(),
    inject[taskList],
    WithSessionRefiner(inject[ErrorHandler], sessionRepo)
  )

  "GET /" should {
    "return 200" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)

      val result = taskListController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)

      val result = taskListController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

}
