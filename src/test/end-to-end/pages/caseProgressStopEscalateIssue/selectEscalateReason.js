'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// CW confirm case stopped
module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#registrarEscalateReason'});
    await I.selectOption({css: '#registrarEscalateReason'}, '1: referrals');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
