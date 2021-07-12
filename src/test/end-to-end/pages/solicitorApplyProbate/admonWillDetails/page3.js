'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;

    await I.waitForElement('#solsEntitledMinority');
    await I.runAccessibilityTest();
    await I.click(`#solsEntitledMinority_${admonWillDetailsConfig.optionNo}`);
    await I.click(`#solsDiedOrNotApplying_${admonWillDetailsConfig.optionYes}`);
    await I.click(`#solsResiduary_${admonWillDetailsConfig.optionYes}`);
    await I.waitForElement('#solsResiduaryType');
    await I.selectOption('#solsResiduaryType', admonWillDetailsConfig.page3_legateeAndDevisee);
    await I.click(`#solsLifeInterest_${admonWillDetailsConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.goButton);
};
