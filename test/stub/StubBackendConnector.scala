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

package stub

import connectors.BackendConnector
import models.{FORLoginResponse, SubmissionDraft}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
case class StubBackendConnector() extends BackendConnector {

  var draft: Option[(String, SubmissionDraft)] = None

  override def verifyCredentials(refNumber: String, postcode: String)(implicit
    hc: HeaderCarrier
  ): Future[FORLoginResponse] = ???

  def retrieveFORType(referenceNumber: String)(implicit hc: HeaderCarrier): Future[String] =
    Future.successful("FOR6010")

  override def saveAsDraft(referenceNumber: String, submissionDraft: SubmissionDraft)(implicit
    hc: HeaderCarrier
  ): Future[Unit] = {
    draft = Some(referenceNumber -> submissionDraft)
    Future.unit
  }

  override def loadSubmissionDraft(referenceNumber: String)(implicit
    hc: HeaderCarrier
  ): Future[Option[SubmissionDraft]] =
    Future.successful(draft.filter(_._1 == referenceNumber).map(_._2))

  override def deleteSubmissionDraft(referenceNumber: String)(implicit hc: HeaderCarrier): Future[Int] = {
    val deletedCount = draft match {
      case Some((`referenceNumber`, _)) =>
        draft = None
        1
      case _                            => 0
    }
    Future.successful(deletedCount)
  }

}
