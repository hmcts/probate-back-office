'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.runAccessibilityTest();

    await I.click(`#willAccessOriginal-${grantOfProbateConfig.optionYes}`);

    await I.fillField('#originalWillSignedDate-day', grantOfProbateConfig.page1_originalWillSignedDate_day);
    await I.fillField('#originalWillSignedDate-month', grantOfProbateConfig.page1_originalWillSignedDate_month);
    await I.fillField('#originalWillSignedDate-year', grantOfProbateConfig.page1_originalWillSignedDate_year);

    await I.click(`#willHasCodicils-${grantOfProbateConfig.optionYes}`);
    const addBtn = {css: '#codicilAddedDateList button'};
    await I.waitForVisible(addBtn);
    await I.scrollTo(addBtn);
    await I.waitForClickable(addBtn);
    await I.click(addBtn);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25);
    }

    await I.fillField('#codicilAddedDateList_0_dateCodicilAdded-day', grantOfProbateConfig.page1_codicilDate_day);
    await I.fillField('#codicilAddedDateList_0_dateCodicilAdded-month', grantOfProbateConfig.page1_codicilDate_month);
    await I.fillField('#codicilAddedDateList_0_dateCodicilAdded-year', grantOfProbateConfig.page1_codicilDate_year);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
