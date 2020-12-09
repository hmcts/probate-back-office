'use strict';

const testConfig = require('src/test/config.js');
const markForIssueConfig = require('./markForIssueConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(markForIssueConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);
    await I.click(`#boExaminationChecklistQ1-${markForIssueConfig.list1_text}`);
    await I.click(`#boExaminationChecklistQ2-${markForIssueConfig.list2_text}`);
    await I.click(`#boExaminationChecklistRequestQA-${markForIssueConfig.list3_text}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
