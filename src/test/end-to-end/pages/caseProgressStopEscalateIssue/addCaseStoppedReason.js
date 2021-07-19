'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForEnabled({css: 'div.panel button'});
    await I.click({css: 'div.panel button'});
    await I.waitForElement({css: '#boCaseStopReasonList_0_caseStopReason'});
    await I.selectOption({css: '#boCaseStopReasonList_0_caseStopReason'}, '1: ExecNotAccountedFor');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
