/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.aboutYourLeaseOrTenure

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutYourLeaseOrTenure.TradeServiceDescriptionForm.tradeServicesDescriptionForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree.updateAboutLeaseOrAgreementPartThree
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartThree, TradeServices, TradeServicesDetails}
import navigation.AboutYourLeaseOrTenureNavigator
import navigation.identifiers.TradeServicesDescriptionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutYourLeaseOrTenure.tradeServicesDescription

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TradeServicesDescriptionController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYourLeaseOrTenureNavigator,
  tradeServicesDescriptionView: tradeServicesDescription,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show(index: Option[Int]): Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    val existingDetails: Option[TradeServicesDetails] = for {
      idx               <- index
      existingServices  <- request.sessionData.aboutLeaseOrAgreementPartThree.map(_.tradeServices)
      requestedServices <- existingServices.lift(idx)
    } yield requestedServices.details
    audit.sendChangeLink("TradeServicesDescription")

    Ok(
      tradeServicesDescriptionView(
        existingDetails.fold(tradeServicesDescriptionForm)(tradeServicesDescriptionForm.fill),
        index,
        getBackLink(request, index.getOrElse(0)),
        request.sessionData.toSummary
      )
    )
  }

  def submit(index: Option[Int]) = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[TradeServicesDetails](
      tradeServicesDescriptionForm,
      formWithErrors =>
        BadRequest(
          tradeServicesDescriptionView(
            formWithErrors,
            index,
            getBackLink(request, index.getOrElse(0)),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedDetails =
          request.sessionData.aboutLeaseOrAgreementPartThree.fold(
            AboutLeaseOrAgreementPartThree(tradeServices = IndexedSeq(TradeServices(details = data)))
          ) { aboutLeaseOrAgreementPartThree =>
            val existingSections                                  = aboutLeaseOrAgreementPartThree.tradeServices
            val requestedSection                                  = index.flatMap(existingSections.lift)
            val updatedSections: (Int, IndexedSeq[TradeServices]) = requestedSection.fold {
              val defaultSection   = TradeServices(data)
              val appendedSections = existingSections.appended(defaultSection)
              appendedSections.indexOf(defaultSection) -> appendedSections
            } { sectionToUpdate =>
              val indexToUpdate = existingSections.indexOf(sectionToUpdate)
              indexToUpdate -> existingSections
                .updated(indexToUpdate, sectionToUpdate.copy(details = data))
            }
            aboutLeaseOrAgreementPartThree
              .copy(
                tradeServicesIndex = updatedSections._1,
                tradeServices = updatedSections._2
              )
          }
        val updatedData    = updateAboutLeaseOrAgreementPartThree(_ => updatedDetails)
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(TradeServicesDescriptionId, updatedData).apply(updatedData)))

      }
    )
  }

  def getBackLink(request: SessionRequest[AnyContent], index: Int): String =
    request.getQueryString("from") match {
      case Some("Change") => controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(index).url
      case _              => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
    }
}
