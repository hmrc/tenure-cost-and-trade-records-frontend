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

package connectors

import com.google.inject.ImplementedBy
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions.auditHeaderCarrier
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditChannel, AuditConnector, AuditResult, DatastreamMetrics}
import uk.gov.hmrc.play.audit.model.DataEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TctrAuditConnector])
trait Audit extends AuditConnector {

  private val AUDIT_SOURCE = "tenure-cost-and-trade-records-frontend"

  def sendContinueNextPage(url: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Unit =
    sendEventMap("ContinueNextPage", Map("url" -> url), hc.toAuditTags())

  private def sendEventMap(event: String, detail: Map[String, String], tags: Map[String, String])(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[AuditResult] = {
    val de = DataEvent(auditSource = AUDIT_SOURCE, auditType = event, tags = tags, detail = detail)
    sendEvent(de)
  }
}

object Audit {
  val referenceNumber = "referenceNumber"
  val address         = "address"
  val formOfReturn    = "for"
}

@Singleton
class TctrAuditConnector @Inject() (
  val auditingConfig: AuditingConfig,
  val auditChannel: AuditChannel,
  val datastreamMetrics: DatastreamMetrics
)(implicit val ec: ExecutionContext)
    extends Audit {}
