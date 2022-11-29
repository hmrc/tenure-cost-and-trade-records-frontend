/* overides jshint */
/*******************/
/* jshint -W079 */
/* jshint -W009 */
/* jshint -W098 */
/******************/

// set namespaces (remember to add new namespaces to .jshintrc)
var VoaFor = {};
var VoaCommon = {};
var VoaFeedback = {};
var VoaMessages = {};
var VoaAlerts = {};
var VoaRadioToggle = {};
var ref;

(function ($) {
    'use strict';
    $(document).ready(function () {

        /** Init functions **/
        //voaFor.js
        VoaFor.showHistoryBackLink();
        VoaFor.printPageShouldPrintOnLoad();
        VoaFor.addField();
        VoaFor.removeField();
        VoaFor.addFieldMulti();
        VoaFor.removeFieldMulti();
        VoaFor.selectMobile();
        VoaFor.isEdit();
        VoaFor.getReferrer();
        VoaFor.addMultiButtonState();
        VoaFor.doYouOwnTheProperty();
        VoaFor.setHelpGDSClasses();

        //feedback.js
        VoaFeedback.toggleHelp();
        VoaFeedback.helpForm();

        //intelAlerts.js
        VoaAlerts.intelAlert();

        //radioToggle.js
        VoaRadioToggle.toggleFieldsBasedOnCheckedRadioButton();

        //common.js
        VoaCommon.anchorFocus();
        VoaCommon.details();
        VoaCommon.characterCount();
    });

})(jQuery);
