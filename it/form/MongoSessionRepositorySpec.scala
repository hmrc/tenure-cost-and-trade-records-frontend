/*
 * Copyright 2022 HM Revenue & Customs
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

package form

import java.util.UUID
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import MongoSessionRepositorySpecData._
import form.persistence.MongoSessionRepository
import org.scalatest.OptionValues

import scala.concurrent.ExecutionContext

class MongoSessionRepositorySpec extends PlaySpec with OptionValues  with FutureAwaits
  with DefaultAwaitTimeout with GuiceOneAppPerSuite {

  def mongoSessionrepository() = app.injector.instanceOf[MongoSessionRepository]

  implicit def ec = app.injector.instanceOf[ExecutionContext]


  "Mongo session repository" should {
    "Store data in mongo" in {

      val cacheId = UUID.randomUUID().toString
      val formId = "formId"
      val testObject = MongoSessionRepositorySpecData(name = "John", buildingNumber = 100)

      await(mongoSessionrepository().cache(cacheId, formId, testObject)(format))

      val res = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, formId)(format))
      res.value mustBe(testObject)

    }

    "store multiple pages in mongo not afection other" in {
      val cacheId = UUID.randomUUID().toString
      val page1 = "page1"
      val page2 = "page2"
      val testObject1 = MongoSessionRepositorySpecData(name = "John", buildingNumber = 100)
      val testObject2 = MongoSessionRepositorySpecData(name = "Peter", buildingNumber = -200)

      await(mongoSessionrepository().cache(cacheId, page1, testObject1)(format))
      await(mongoSessionrepository().cache(cacheId, page2, testObject2)(format))


      val testObjectFromDatabase1 = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, page1)(format))
      testObjectFromDatabase1.value mustBe(testObject1)
      val testObjectFromDatabase2 = await(mongoSessionrepository().fetchAndGetEntry[MongoSessionRepositorySpecData](cacheId, page2)(format))
      testObjectFromDatabase2.value mustBe(testObject2)
    }
  }

}

case class MongoSessionRepositorySpecData(name: String, buildingNumber: Int)

object MongoSessionRepositorySpecData {

  implicit val format = Json.format[MongoSessionRepositorySpecData]

}
