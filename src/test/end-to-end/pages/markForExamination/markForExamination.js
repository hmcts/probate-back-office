'use strict';

const testConfig = require('src/test/config.js');
const markForExaminationConfig = require('./markForExaminationConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(markForExaminationConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click(`#boEmailDocsReceivedNotification-${markForExaminationConfig.list1_text}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
