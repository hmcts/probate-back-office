'use strict';

const testConfig = require('src/test/config.js');
const markForIssueConfig = require('./markForIssueConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(markForIssueConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);
    I.click(`#boExaminationChecklistQ1-${markForIssueConfig.list1_text}`);
    I.click(`#boExaminationChecklistQ2-${markForIssueConfig.list2_text}`);
    I.click(`#boExaminationChecklistRequestQA-${markForIssueConfig.list3_text}`);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
