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

package test

import org.mongodb.scala.SingleObservableFuture
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.reflect.ClassTag

/**
  * @author Yuriy Tumakha
  */
abstract class TCTRMongoSpec[E, R <: PlayMongoRepository[E]: ClassTag] extends TCTRAppSpec with DefaultPlayMongoRepositorySupport[E]:

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(fakeAppConfiguration)
      .overrides(api.inject.bind[MongoComponent].toInstance(mongoComponent))
      .build()

  val mongoRepository: R = inject[R]

  override protected val repository: PlayMongoRepository[E] = mongoRepository

  override protected def afterAll(): Unit =
    mongoComponent.database.drop().toFutureOption().futureValue
    mongoComponent.client.close()
