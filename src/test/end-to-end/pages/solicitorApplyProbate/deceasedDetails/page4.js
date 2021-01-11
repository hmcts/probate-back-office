'use strict';

const deceasedDetailsConfig = require('./deceasedDetailsConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willDispose');
    await I.click(`#willDispose-${deceasedDetailsConfig.optionYes}`);
    await I.click(`#englishWill-${deceasedDetailsConfig.optionYes}`);
    await I.click(`#appointExec-${deceasedDetailsConfig.optionYes}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
