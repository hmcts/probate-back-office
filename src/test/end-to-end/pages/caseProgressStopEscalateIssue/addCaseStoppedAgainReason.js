'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#boCaseStopReasonList_0_caseStopReason'});
    await I.selectOption({css: '#boCaseStopReasonList_0_caseStopReason'}, '25: DocumentsRequired');
    await I.waitForElement({css: '#boCaseStopReasonList_0_caseStopSubReasonMyStopReason'});
    await I.selectOption({css: '#boCaseStopReasonList_0_caseStopSubReasonMyStopReason'}, 'PA11');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
