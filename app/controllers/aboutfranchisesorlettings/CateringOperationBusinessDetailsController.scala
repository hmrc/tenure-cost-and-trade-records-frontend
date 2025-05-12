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

package controllers.aboutfranchisesorlettings

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationBusinessDetails6030Form.cateringOperationBusinessDetails6030Form
import form.aboutfranchisesorlettings.CateringOperationBusinessDetailsForm.cateringOperationBusinessDetailsForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{ConcessionBusinessDetails, ConcessionIncomeRecord}
import models.ForType
import models.ForType.*
import models.Session
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationBusinessPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationBusinessDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  private def forType(implicit request: SessionRequest[AnyContent]): ForType =
    request.sessionData.forType

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[ConcessionBusinessDetails] = for {
      requestedIndex            <- index
      allRecords                <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      existingRecord            <- allRecords.lift(requestedIndex)
      concessionBusinessDetails <- existingRecord match {
                                     case concession: ConcessionIncomeRecord => concession.businessDetails
                                     case _                                  => None
                                   }
    } yield concessionBusinessDetails

    audit.sendChangeLink("ConcessionBusinessDetails")
    Ok(
      cateringOperationDetailsView(
        if (forType == FOR6030) {
          existingDetails.fold(cateringOperationBusinessDetails6030Form)(
            cateringOperationBusinessDetails6030Form.fill
          )
        } else {
          existingDetails.fold(cateringOperationBusinessDetailsForm)(
            cateringOperationBusinessDetailsForm.fill
          )
        },
        index,
        "concessionDetails",
        "cateringOperationOrLettingAccommodationDetails",
        getBackLink(request.sessionData, index),
        request.sessionData.toSummary,
        forType
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ConcessionBusinessDetails](
      if (forType == FOR6030) {
        cateringOperationBusinessDetails6030Form
      } else {
        cateringOperationBusinessDetailsForm
      },
      formWithErrors =>
        BadRequest(
          cateringOperationDetailsView(
            formWithErrors,
            index,
            "concessionDetails",
            "cateringOperationOrLettingAccommodationDetails",
            getBackLink(request.sessionData, index),
            request.sessionData.toSummary,
            request.sessionData.forType
          )
        ),
      data => {
        val idx         = index.getOrElse(0)
        val updatedData = updateAboutFranchisesOrLettings { aboutFranchisesOrLettings =>
          if (aboutFranchisesOrLettings.rentalIncome.exists(_.isDefinedAt(idx))) {
            val updatedRentalIncome = aboutFranchisesOrLettings.rentalIncome.map { records =>
              records.updated(idx, records(idx).asInstanceOf[ConcessionIncomeRecord].copy(businessDetails = Some(data)))
            }
            aboutFranchisesOrLettings.copy(rentalIncome = updatedRentalIncome, rentalIncomeIndex = idx)
          } else {
            aboutFranchisesOrLettings
          }
        }

        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(CateringOperationBusinessPageId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(answers: Session, maybeIndex: Option[Int]): String =
    answers.forType match {
      case FOR6015 | FOR6016 =>
        controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url
      case FOR6030           =>
        controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show().url
      case _                 =>
        maybeIndex match {
          case Some(index) if index > 0 =>
            controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(index - 1).url
          case _                        =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
        }
    }

}
