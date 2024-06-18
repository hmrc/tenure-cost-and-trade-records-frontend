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
import models.submissions.downloadFORTypeForm.DownloadPDF
import org.scalatest.matchers.must.Matchers._
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours

class DownloadPDFViewSpec extends QuestionViewBehaviours[DownloadPDF] {

  val messageKeyPrefix    = "downloadPdf"
  val testReferenceNumber = "12345678"

  override val form = DownloadPDFForm.downloadPDFForm

  def createView6010      = () => downloadPDFView("FOR6010", testReferenceNumber)(fakeRequest, messages)
  def createView6011      = () => downloadPDFView("FOR6011", testReferenceNumber)(fakeRequest, messages)
  def createView6015      = () => downloadPDFView("FOR6015", testReferenceNumber)(fakeRequest, messages)
  def createView6016      = () => downloadPDFView("FOR6016", testReferenceNumber)(fakeRequest, messages)
  def createView6020      = () => downloadPDFView("FOR6020", testReferenceNumber)(fakeRequest, messages)
  def createView6030      = () => downloadPDFView("FOR6030", testReferenceNumber)(fakeRequest, messages)
  def createView6045      = () => downloadPDFView("FOR6045", testReferenceNumber)(fakeRequest, messages)
  def createView6046      = () => downloadPDFView("FOR6046", testReferenceNumber)(fakeRequest, messages)
  def createView6048      = () => downloadPDFView("FOR6048", testReferenceNumber)(fakeRequest, messages)
  def createView6076      = () => downloadPDFView("FOR6076", testReferenceNumber)(fakeRequest, messages)
  def createViewNoForType = () => downloadPDFView("", testReferenceNumber)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[DownloadPDF]) =>
    downloadPDFView("FOR6010", testReferenceNumber)(fakeRequest, messages)

  "download pdf view" must {

    behave like normalPage(createView6010, messageKeyPrefix)

    "has a link marked with back.link.label leading to the Download Reference Page" in {
      val doc          = asDocument(createView6010())
      val backlinkText = doc.select("a[class=govuk-back-link]").text()
      backlinkText mustBe messages("back.link.label")
      val backlinkUrl  = doc.select("a[class=govuk-back-link]").attr("href")
      backlinkUrl mustBe controllers.downloadFORTypeForm.routes.DownloadPDFReferenceNumberController.show().url
    }

    "contain link to form download 6010" in {
      val doc = asDocument(createView6010())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6010.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6010.url")))
    }

    "contain link to form download 6011" in {
      val doc = asDocument(createView6011())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6011.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6011.url")))
    }

    "contain link to form download 6015" in {
      val doc = asDocument(createView6015())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6015.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6015.url")))
    }

    "contain link to form download 6016" in {
      val doc = asDocument(createView6016())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6016.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6016.url")))
    }

    "contain link to form download 6020" in {
      val doc = asDocument(createView6020())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6020.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6020.url")))
    }

    "contain link to form download 6030" in {
      val doc = asDocument(createView6030())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6030.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6030.url")))
    }

    "contain link to form download 6045" in {
      val doc = asDocument(createView6045())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6045.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6045.url")))
    }

    "contain link to form download 6046" in {
      val doc = asDocument(createView6046())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6046.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6046.url")))
    }

    "contain link to form download 6048" in {
      val doc = asDocument(createView6048())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6048.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6048.url")))
    }

    "contain link to form download 6076" in {
      val doc = asDocument(createView6076())
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6076.label")))
      assert(doc.select("a[class=govuk-link]").toString.contains(messages("downloadPdf.6076.url")))
    }

    "No FOR type found page" in {
      val doc = asDocument(createViewNoForType())
      assert(doc.toString.contains(messages("downloadPdf.no.download")))
      assert(doc.toString.contains(messages("downloadPdf.retry")))
    }
  }
}
