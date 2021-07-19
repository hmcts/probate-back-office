'use strict';

const testConfig = require('src/test/config.js');
const markForExaminationConfig = require('./markForExaminationConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(markForExaminationConfig.waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    await I.click(`#boEmailDocsReceivedNotification_${markForExaminationConfig.list1_text}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
