'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForClickable({css: `#dispenseWithNoticeLeaveGiven-${grantOfProbateConfig.page3_dispenseWithNoticeLeaveGiven}`});
    await I.runAccessibilityTest();
    await I.click(`#dispenseWithNoticeLeaveGiven-${grantOfProbateConfig.page3_dispenseWithNoticeLeaveGiven}`);

    await I.fillField('#dispenseWithNoticeOverview', grantOfProbateConfig.page3_dispenseWithNoticeOverview);
    await I.fillField('#dispenseWithNoticeSupportingDocs', grantOfProbateConfig.page3_dispenseWithNoticeSupportingDocs);

    await I.click('#dispenseWithNoticeOtherExecsList > div > button');
    await I.fillField('#dispenseWithNoticeOtherExecsList_0_notApplyingExecutorName', grantOfProbateConfig.page3_dispenseWithNoticeName);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
