'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.runAccessibilityTest();
    await I.click(`#willAccessOriginal_${admonWillDetailsConfig.optionYes}`);
    await I.click(`#willHasCodicils_${admonWillDetailsConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
