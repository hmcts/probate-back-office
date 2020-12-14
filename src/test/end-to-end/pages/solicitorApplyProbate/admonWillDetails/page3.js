'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsAdditionalInfo');
    await I.fillField('#solsAdditionalInfo', admonWillDetailsConfig.page3_applicationNotes);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
