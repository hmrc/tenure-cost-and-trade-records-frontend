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

import models._
import models.submissions.{AddressConnectionType, AddressConnectionTypes, CurrentPropertyUse, CurrentPropertyUsed, UserType, UserTypes, WebsiteForPropertyDetails}
import play.api.data.FormError
import play.api.data.format.Formatter

object Formats {

  def namedEnumFormatter[T <: NamedEnum](named:NamedEnumSupport[T], missingCode:String): Formatter[T] = new Formatter[T] {

    override val format = Some((missingCode, Nil))

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      val resOpt = for {
        keyVal <- data.get(key)
        enumTypeValue <- named.fromName(keyVal)
      } yield {
          Right(enumTypeValue)
        }
      resOpt.getOrElse(Left(Seq(FormError(key, missingCode, Nil))))
    }

    def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
  }

  def namedEnumFormatterWithKeys[T <: NamedEnum](named:NamedEnumSupport[T], missingCodes:Map[String, String]): Formatter[T] = new Formatter[T] {

    override val format = Some((Errors.required, Nil))

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      val resOpt = for {
        keyVal <- data.get(key)
        enumTypeValue <- named.fromName(keyVal)
      } yield {
        Right(enumTypeValue)
      }
      val maybeString = missingCodes.get(key)
      resOpt.getOrElse(Left(Seq(FormError(key, maybeString.getOrElse(Errors.required), Nil))))
    }

    def unbind(key: String, value: T): Map[String, String] = Map(key -> value.name)
  }

  implicit val userTypeFormat: Formatter[UserType] = namedEnumFormatter(UserTypes, Errors.userTypeRequired)
  implicit val aboutYourPropertyFormat: Formatter[CurrentPropertyUsed] = namedEnumFormatter(CurrentPropertyUse, Errors.currentOccupierName)
  implicit val addressConnectionTypeFormatter: Formatter[AddressConnectionType] = namedEnumFormatter(AddressConnectionTypes, Errors.isConnectedError)
}
