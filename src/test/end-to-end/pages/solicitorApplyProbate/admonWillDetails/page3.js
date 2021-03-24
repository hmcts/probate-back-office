'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsEntitledMinority');
    await I.runAccessibilityTest();
    await I.click(`#solsEntitledMinority-${admonWillDetailsConfig.optionNo}`);
    await I.click(`#immovableEstate-${admonWillDetailsConfig.optionYes}`);
    await I.click(`#solsDiedOrNotApplying-${admonWillDetailsConfig.optionYes}`);
    await I.click(`#solsResiduary-${admonWillDetailsConfig.optionYes}`);
    await I.waitForElement('#solsResiduaryType');
    await I.selectOption('#solsResiduaryType', admonWillDetailsConfig.page2_legateeAndDevisee);
    await I.click(`#solsLifeInterest-${admonWillDetailsConfig.optionNo}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.fillField('#solsAdditionalInfo', admonWillDetailsConfig.page4_applicationNotes);
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
