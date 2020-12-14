'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsConfirmSignSOT1');

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
