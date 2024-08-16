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

package models

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{Format, Json}

/**
  * @author Yuriy Tumakha
  */
class Scala3EnumFormatSpec extends AnyFlatSpec with should.Matchers {

  enum Color:
    case Red, Green, Blue

  implicit val format: Format[Color] = Scala3EnumFormat.format

  import Color.*

  "Scala3EnumFormat.format" should "serialize Scala 3 enum to json" in {
    val obj  = Seq(Green, Blue)
    val json = Json.toJson(obj)
    json.as[Seq[Color]]  shouldBe obj
    Json.stringify(json) shouldBe """["Green","Blue"]"""
  }

  it should "deserialize Scala 3 enum from json" in {
    val obj = Json.parse("\"Red\"").as[Color]
    obj shouldBe Red
  }

}
