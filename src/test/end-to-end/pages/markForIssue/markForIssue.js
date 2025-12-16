'use strict';

const testConfig = require('src/test/config.js');
const markForIssueConfig = require('./markForIssueConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {

    const I = this;
    await I.waitForText(markForIssueConfig.waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);
    await I.waitForElement({css: `#boExaminationChecklistQ1_${markForIssueConfig.list1_text}`});
    await I.waitForEnabled({css: `#boExaminationChecklistQ1_${markForIssueConfig.list1_text}`});
    await I.click(`#boExaminationChecklistQ1_${markForIssueConfig.list1_text}`);
    await I.click(`#boExaminationChecklistQ2_${markForIssueConfig.list2_text}`);
    await I.click(`#boExaminationChecklistRequestQA_${markForIssueConfig.list3_text}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
