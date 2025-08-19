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

package models.pages

import play.api.mvc.QueryStringBindable
import models.pages.Section.*

import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
enum MaxListItemsPage(val section: Section):

  def itemsInPluralKey: String = s"maxListItems.$this"
  def paragraphMsgKey: String  = s"maxListItems.addedMaximum$this.p1"

  case AccommodationUnits extends MaxListItemsPage(accommodation)
  case TradeServices extends MaxListItemsPage(aboutYourLeaseOrTenure)
  case ServicesPaidSeparately extends MaxListItemsPage(aboutYourLeaseOrTenure)
  case BunkerFuelCards extends MaxListItemsPage(aboutYourTradingHistory)
end MaxListItemsPage

object MaxListItemsPage:

  given queryStringBinder(using
    stringBinder: QueryStringBindable[String]
  ): QueryStringBindable[MaxListItemsPage] =
    new QueryStringBindable[MaxListItemsPage]:
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, MaxListItemsPage]] =
        stringBinder
          .bind(key, params)
          .map {
            _.flatMap(page => Try(MaxListItemsPage.valueOf(page)).toEither)
          }
          .map {
            case Right(page) => Right(page)
            case _           => Left("Unable to bind a MaxListItemsPage")
          }

      override def unbind(key: String, page: MaxListItemsPage): String =
        s"$key=$page"
