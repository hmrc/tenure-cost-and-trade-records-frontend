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

import actions.SessionRequest
import com.google.inject.ImplementedBy
import models.Session
import models.audit.{ChangeLinkAudit, SavedAsDraftEvent}
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions.auditHeaderCarrier
import uk.gov.hmrc.play.audit.http.config.AuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditChannel, AuditConnector, AuditResult, DatastreamMetrics}
import uk.gov.hmrc.play.audit.model.DataEvent

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TctrAuditConnector])
trait Audit extends AuditConnector {

  implicit def ec: ExecutionContext

  private val AUDIT_SOURCE = "tenure-cost-and-trade-records-frontend"

  def apply(event: String, detail: Map[String, String])(implicit hc: HeaderCarrier): Future[AuditResult] = {
    val tags = hc.toAuditTags()
    val de   = DataEvent(auditSource = AUDIT_SOURCE, auditType = event, tags = tags, detail = detail)
    sendEvent(de)
  }

  def sendContinueNextPage(session: Session, url: String)(implicit hc: HeaderCarrier): Unit = {
    val continueNextPageJson = Json.toJson(session).as[JsObject] + ("nextPageURL" -> Json.toJson(url))
    sendExplicitAudit("ContinueNextPage", continueNextPageJson)
  }

  def sendSavedAsDraft(savedAsDraftEvent: SavedAsDraftEvent)(implicit hc: HeaderCarrier): Unit =
    sendExplicitAudit("SavedAsDraft", savedAsDraftEvent)

  def sendChangeLink(pageID: String)(implicit request: SessionRequest[AnyContent], hc: HeaderCarrier): Unit =
    if (request.getQueryString("from").contains("CYA")) {
      sendExplicitAudit("CyaChangeLink", ChangeLinkAudit(request.sessionData.forType.toString, request.uri, pageID))
    }

}

object Audit {
  val referenceNumber = "referenceNumber"
  val address         = "address"
  val formOfReturn    = "forType"
  val language        = "language"

  def languageJson(implicit messages: Messages): JsObject =
    Json.obj(Audit.language -> messages.lang.language)

}

@Singleton
class TctrAuditConnector @Inject() (
  val auditingConfig: AuditingConfig,
  val auditChannel: AuditChannel,
  val datastreamMetrics: DatastreamMetrics
)(implicit val ec: ExecutionContext)
    extends Audit {}
