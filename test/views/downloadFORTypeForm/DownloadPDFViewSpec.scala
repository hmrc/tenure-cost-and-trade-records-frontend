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

package views.downloadFORTypeForm

import form.downloadFORTypeForm.DownloadPDFForm
import form.requestReferenceNumber.RequestReferenceNumberForm
import models.submissions.downloadFORTypeForm.DownloadPDF
import models.submissions.requestReferenceNumber.RequestReferenceNumber
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class DownloadPDFViewSpec extends QuestionViewBehaviours[DownloadPDF] {

  val messageKeyPrefix = "downloadPdf"

  override val form = DownloadPDFForm.downloadPDFForm

  def createView = () => downloadPDFView("FOR6010")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[DownloadPDF]) => downloadPDFView("FOR6010")(fakeRequest, messages)

  "download pdf view" must {

    behave like normalPage(createView, messageKeyPrefix)

    "has a link marked with back.link.label leading to the Download Reference Page" in {
      val doc          = asDocument(createView())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.downloadFORTypeForm.routes.DownloadPDFReferenceNumberController.show().url
    }
  }
}
