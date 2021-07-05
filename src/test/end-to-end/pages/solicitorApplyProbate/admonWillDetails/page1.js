'use strict';

const admonWillDetailsConfig = require('./admonWillDetails');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#willAccessOriginal');
    await I.runAccessibilityTest();
    await I.click({css: '#willAccessOriginal_No'});
    await I.waitForVisible('#willAccessOriginalHintText');
    await I.waitForVisible('#noOriginalWillAccessReason');
    await I.waitForText(admonWillDetailsConfig.page1_noAccessOriginalWillLabel);

    await I.click({css: `#willAccessOriginal_${admonWillDetailsConfig.optionYes}`});
    await I.waitForInvisible({css: '#willAccessOriginalHintText'});
    await I.waitForInvisible({css: '#noOriginalWillAccessReason'});

    await I.fillField({css: '#originalWillSignedDate-day'}, admonWillDetailsConfig.page1_originalWillSignedDate_day);
    await I.fillField({css: '#originalWillSignedDate-month'}, admonWillDetailsConfig.page1_originalWillSignedDate_month);
    await I.fillField({css: '#originalWillSignedDate-year'}, admonWillDetailsConfig.page1_originalWillSignedDate_year);
    await I.click(`#willAccessOriginal-${admonWillDetailsConfig.optionYes}`);

    await I.click({css: `#willHasCodicils_${admonWillDetailsConfig.optionYes}`});
    const addBtn = {css: '#codicilAddedDateList button'};
    await I.waitForVisible(addBtn);
    await I.scrollTo(addBtn);
    await I.waitForClickable(addBtn);
    await I.click(addBtn);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort);
    }

    await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-day'}, admonWillDetailsConfig.page1_codicilDate_day);
    await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-month'}, admonWillDetailsConfig.page1_codicilDate_month);
    await I.fillField({css: '#codicilAddedDateList_0_dateCodicilAdded-year'}, admonWillDetailsConfig.page1_codicilDate_year);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
