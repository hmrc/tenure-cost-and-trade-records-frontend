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

package actions

import models.Session
import play.api.mvc.{AnyContent, Request, WrappedRequest}

case class SessionRequest[A](
  sessionData: Session,
  request: Request[A]
) extends WrappedRequest[A](request) {}

object SessionRequest:
  extension (request: SessionRequest[AnyContent])
    def isFromCheckYourAnswer: Boolean = isFrom("CYA")

    def isFromTaskList: Boolean = isFrom("TL")

    def eventualFromFragment: Option[String] =
      hasFrom(ifEmpty = None) { fromValue =>
        if fromValue.nonEmpty
        then
          val splitted = fromValue.split(";")
          if splitted.size > 1 then Some(splitted(1)) else None
        else None
      }

    private def isFrom(pageId: String): Boolean =
      hasFrom(ifEmpty = false)(_.contains(pageId))

    private def hasFrom[T](ifEmpty: T)(fn: String => T) =
      request
        .getQueryString("from")
        .map(fn)
        .getOrElse {
          request.body.asFormUrlEncoded
            .map { submittedData =>
              submittedData
                .get("from")
                .flatMap(_.headOption)
                .map(fn)
                .getOrElse(ifEmpty)
            }
            .getOrElse(ifEmpty)
        }
