'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#boCaseStopReasonList_0_caseStopReason'});
    await I.selectOption({css: '#boCaseStopReasonList_0_caseStopReason'}, '10: DocumentsRequired');
    await I.waitForElement({css: '#boCaseStopReasonList_0_caseStopSubReasonDocRequired'});
    await I.selectOption({css: '#boCaseStopReasonList_0_caseStopSubReasonDocRequired'}, '9: PA11');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
