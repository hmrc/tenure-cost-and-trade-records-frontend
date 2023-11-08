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

package controllers.form

import form.{Errors, PostcodeMapping}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.data.{Form, FormError}

class PostcodeMappingSpec extends AnyFlatSpec with should.Matchers with EitherValues with TableDrivenPropertyChecks {

  val positiveTestData = Table(
    ("raw postcode", "formated postcode"),
    ("   ML7 +++\n  8LQ   ++", "ML7 8LQ"),
    ("ML7 8LQ", "ML7 8LQ"),
    ("ML7.8LQ", "ML7 8LQ"),
    ("ML7+8LQ", "ML7 8LQ"),
    ("SW1A.1AA", "SW1A 1AA"),
    ("XM4.5HQ", "XM4 5HQ"),
    ("M L 7    8 L Q ", "ML7 8LQ"),
    ("m l 7 +\n  8 l q ", "ML7 8LQ")
  )
  val negativeTestData = Table(
    ("raw postcode", "error type", "error message"),
    ("", "requiredError", Errors.postcodeRequired),
    ("123456789", "maxLengthError", Errors.postcodeMaxLength),
    ("BS12D", "formatError", Errors.postcodeInvalid),
    ("XYZ1234", "formatError", Errors.postcodeInvalid)
  )

  val postcode = PostcodeMapping.postcode()

  "PostcodeMapper" should "Map correct postcode" in {
    val formData = Map("" -> "BN12 4AX")
    postcode.bind(formData).value shouldBe "BN12 4AX"
  }

  it               should "sucessfully format and validate all correct postcodes" in {
    forAll(positiveTestData) { (rawPostcode: String, formattedPostcode: String) =>
      val form   = Form("postcode" -> PostcodeMapping.postcode())
      val result = form.bind(Map("postcode" -> rawPostcode)).value
      result shouldBe Some(formattedPostcode)
    }
  }

  it               should "produce an error for each invalid postcode" in {
    forAll(negativeTestData) { (rawPostcode: String, _, errorMessage: String) =>
      val result = Form("postcode" -> PostcodeMapping.postcode()).bind(Map("postcode" -> rawPostcode))
      result.errors should contain oneElementOf List(FormError("postcode", errorMessage))
    }
  }
}
