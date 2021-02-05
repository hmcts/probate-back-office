'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForClickable({css: `#dispenseWithNoticeLeaveGiven-${grantOfProbateConfig.optionNo}`});
    await I.runAccessibilityTest();
    await I.click(`#dispenseWithNoticeLeaveGiven-${grantOfProbateConfig.optionNo}`);

    await I.fillField('#executorWithPowerReserved', grantOfProbateConfig.page3_executorWithPowerReserved);
    await I.fillField('#dispenseWithNoticeOverview', grantOfProbateConfig.page3_dispenseWithNoticeOverview);
    await I.fillField('#dispenseWithNoticeSupportingDocs', grantOfProbateConfig.page3_dispenseWithNoticeSupportingDocs);

    await I.click(`#dispenseWithNoticeOtherExecs-${grantOfProbateConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
