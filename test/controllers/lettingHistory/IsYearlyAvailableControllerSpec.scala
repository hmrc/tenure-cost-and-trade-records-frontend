package controllers.lettingHistory

import models.Session
import models.submissions.lettingHistory.{IntendedLettings, LettingHistory}
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.http.MimeTypes.HTML
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Writes
import play.api.mvc.Codec.utf_8 as UTF_8
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import views.html.lettingHistory.isYearlyAvailable as IsYearlyAvailableView
import utils.JsoupHelpers.*

class IsYearlyAvailableControllerSpec extends LettingHistoryControllerSpec:

  "the IsYearlyAvailable controller" when {
    "the user hasn't answered yet"  should {
      "be handling GET and reply 200 with the HTML form having unchecked radios" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                         shouldBe "lettingHistory.isYearlyAvailable.eitherMeetsCriteriaOrHasNotStopped.heading"
        page.backLink                        shouldBe routes.HowManyNightsController.show.url
        page.radio("yes").hasAttr("checked") shouldBe false
        page.radio("no").hasAttr("checked")  shouldBe false
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing answer!
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.isYearlyAvailable.required"
      }
      "be handling POST answer='yes' by replying 303 redirect to the 'Last Rent' page" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "yes"
          )
        )
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe "/path/to/length-of-trading"
        verify(repository, once).saveOrUpdate(data.capture())(any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.isYearlyAvailable.value shouldBe true
      }
    }
    "the user has already answered" should {
      "be handling GET and reply 200 with the HTML form having checked radios" in new ControllerFixture(
        hasStopped = Some(true),
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF_8.charset
        val page = contentAsJsoup(result)
        page.heading                         shouldBe "lettingHistory.isYearlyAvailable.hasStoppedLetting.heading"
        page.backLink                        shouldBe routes.WhenWasLastLetController.show.url
        page.radio("yes").hasAttr("checked") shouldBe true
        page.radio("no").hasAttr("checked")  shouldBe false
      }
      "be handling invalid POST by replying 400 with error message" in new ControllerFixture(
        hasStopped = Some(false),
        isYearlyAvailable = Some(true)
      ) {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "answer" -> "" // missing answer!
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page = contentAsJsoup(result)
        page.error("answer") shouldBe "lettingHistory.isYearlyAvailable.required"
      }
    }
  }

  trait ControllerFixture(isYearlyAvailable: Option[Boolean] = None, hasStopped: Option[Boolean] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new IsYearlyAvailableController(
      mcc = stubMessagesControllerComponents(),
      navigator = inject[LettingHistoryNavigator],
      theView = inject[IsYearlyAvailableView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedLettings(
                hasStopped = hasStopped,
                isYearlyAvailable = isYearlyAvailable
              )
            )
          )
        )
      ),
      repository
    )
