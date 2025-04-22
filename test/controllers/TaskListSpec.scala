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
import play.api.http.Status
import play.api.test.Helpers._
import stub.StubSessionRepo
import utils.TestBaseSpec
import views.html.taskList.taskList

class TaskListSpec extends TestBaseSpec {

  private val sessionRepo = StubSessionRepo()

  private def taskListController = new TaskListController(
    stubMessagesControllerComponents(),
    inject[taskList],
    WithSessionRefiner(sessionRepo)
  )

  "GET /" should {
    "return 200 (6010)" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)

      val result = taskListController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML (6011)" in {
      sessionRepo.saveOrUpdate(baseFilled6011Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6015)" in {
      sessionRepo.saveOrUpdate(baseFilled6015Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6016)" in {
      sessionRepo.saveOrUpdate(baseFilled6016Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6020)" in {
      sessionRepo.saveOrUpdate(baseFilled6020Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6030)" in {
      sessionRepo.saveOrUpdate(baseFilled6030Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6045)" in {
      sessionRepo.saveOrUpdate(baseFilled6045Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6046)" in {
      sessionRepo.saveOrUpdate(baseFilled6046Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6048)" in {
      sessionRepo.saveOrUpdate(baseFilled6048Session)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
    }

    "return HTML (6076)" in {
      sessionRepo.saveOrUpdate(aboutYourTradingHistory6076YesSession)

      val result = taskListController.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")

    }
  }

}
