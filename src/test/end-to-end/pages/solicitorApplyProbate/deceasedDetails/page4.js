'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willDispose');
    await I.runAccessibilityTest();

    await I.click(`#willDispose_${deceasedDetailsConfig.optionYes}`);
    await I.click(`#englishWill_${deceasedDetailsConfig.optionYes}`);
    await I.click(`#appointExec_${deceasedDetailsConfig.optionYes}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
