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

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutfranchisesorlettings.CateringOperationBusinessDetails6030Form.cateringOperationBusinessDetails6030Form
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{ConcessionBusinessDetails, ConcessionIncomeRecord}
import navigation.AboutFranchisesOrLettingsNavigator
import navigation.identifiers.CateringOperationBusinessPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutfranchisesorlettings.cateringOperationOrLettingAccommodationDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CateringOperationBusinessDetailsController @Inject() ( // 6030 only
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutFranchisesOrLettingsNavigator,
  cateringOperationDetailsView: cateringOperationOrLettingAccommodationDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[ConcessionBusinessDetails] = for {
      requestedIndex            <- index
      allRecords                <- request.sessionData.aboutFranchisesOrLettings.flatMap(_.rentalIncome)
      concessionBusinessDetails <- allRecords
                                     .lift(requestedIndex)
                                     .collect[Option[ConcessionBusinessDetails]] {
                                       case concession: ConcessionIncomeRecord => concession.businessDetails
                                     }
                                     .flatten
    } yield concessionBusinessDetails

    audit.sendChangeLink("ConcessionBusinessDetails")
    Ok(
      cateringOperationDetailsView(
        existingDetails.fold(cateringOperationBusinessDetails6030Form)(cateringOperationBusinessDetails6030Form.fill),
        index,
        "concessionTypeDetails",
        getBackLink(index),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ConcessionBusinessDetails](
      cateringOperationBusinessDetails6030Form,
      formWithErrors =>
        BadRequest(
          cateringOperationDetailsView(
            formWithErrors,
            index,
            "concessionDetails",
            getBackLink(index),
            request.sessionData.toSummary
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

  private def getBackLink(index: Option[Int]): String =
    controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show(index).url

}
