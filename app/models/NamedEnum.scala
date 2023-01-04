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

package models

import play.api.libs.json._
import play.api.mvc.QueryStringBindable

trait NamedEnum {
  def name: String

  def key: String

  def msgKey: String = s"$key.$name"
}

trait NamedEnumSupport[E <: NamedEnum] {

  def all: Seq[E]

  def fromName(name: String): Option[E] =
    all.find {
      _.name.equalsIgnoreCase(name)
    }

  def options = all.map(_.name)

  def unapply(s: String) = all.find(_.name == s)

  implicit val queryStringBindable: QueryStringBindable[E] = new QueryStringBindable[E] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, E]] =
      for {
        vs <- params.get(key)
        v  <- vs.headOption
      } yield unapply(v) match {
        case Some(e) => Right(e)
        case None    => Left(s"Invalid value; expected one of $options")
      }

    override def unbind(key: String, value: E): String = key + "=" + value.name
  }
}

object EnumFormat {
  def apply[T <: NamedEnum](enumObject: NamedEnumSupport[T]): Format[T] =
    Format[T](generateReads(enumObject), generateWrites(enumObject))

  private def generateWrites[T <: NamedEnum](enumObject: NamedEnumSupport[T]): Writes[T] = new Writes[T] {
    def writes(data: T): JsValue =
      JsString(data.name)
  }

  private def generateReads[T <: NamedEnum](enumObject: NamedEnumSupport[T]): Reads[T] = new Reads[T] {
    def reads(json: JsValue): JsResult[T] = json match {
      case JsString(value) =>
        enumObject.fromName(value) match {
          case Some(enumValue) => JsSuccess(enumValue)
          case None            => JsError(s"Value: $value is not valid; expected one of ${enumObject.all}")
        }
      case js              =>
        JsError(s"Invalid Json: expected string, got: $js")
    }
  }
}
