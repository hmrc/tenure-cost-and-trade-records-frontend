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

package navigation.identifiers

import utils.TestBaseSpec

class ConnectedToPropertyIdentifiersSpec extends TestBaseSpec {

  "Connection to property identifiers" when {

    "Identifier for sign in page" in {
      assert(SignInPageId.toString.equals("signInPage"))
    }

    "Identifier for are you still connected page" in {
      assert(AreYouStillConnectedPageId.toString.equals("areYouStillConnectedPage"))
    }

    "Identifier for edit address page" in {
      assert(EditAddressPageId.toString.equals("editAddressPage"))
    }

    "Identifier for connection to the property page" in {
      assert(ConnectionToPropertyPageId.toString.equals("ConnectionToPropertyPage"))
    }

    "Identifier for no reference number page" in {
      assert(NoReferenceNumberPageId.toString.equals("NoReferenceNumberPage"))
    }
  }
}
