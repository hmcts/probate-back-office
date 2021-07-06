'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.waitForElement({css: '#willAccessOriginal'});
    await I.runAccessibilityTest();

    await I.click({css: '#willAccessOriginal_No'});
    await I.waitForVisible({css: '#willAccessOriginalHintText'});
    await I.waitForVisible({css: '#noOriginalWillAccessReason'});
    await I.waitForText(grantOfProbateConfig.page1_noAccessOriginalWillLabel);

    await I.click({css: `#willAccessOriginal_${grantOfProbateConfig.optionYes}`});
    await I.waitForInvisible({css: '#willAccessOriginalHintText'});
    await I.waitForInvisible({css: '#noOriginalWillAccessReason'});

    await I.fillField({css: '#originalWillSignedDate-day'}, grantOfProbateConfig.page1_originalWillSignedDate_day);
    await I.fillField({css: '#originalWillSignedDate-month'}, grantOfProbateConfig.page1_originalWillSignedDate_month);
    await I.fillField({css: '#originalWillSignedDate-year'}, grantOfProbateConfig.page1_originalWillSignedDate_year);

    await I.click({css: `#willHasCodicils_${grantOfProbateConfig.optionYes}`});
    const addBtn = {css: '#codicilAddedDateList button'};
    await I.waitForVisible(addBtn);
    await I.scrollTo(addBtn);
    await I.waitForClickable(addBtn);
    await I.click(addBtn);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort);
    }

    // exui bug - generating multiple elements with same id
    await I.fillField({css: '#dateCodicilAdded-day'}, grantOfProbateConfig.page1_codicilDate_day);
    await I.fillField({css: '#dateCodicilAdded-month'}, grantOfProbateConfig.page1_codicilDate_month);
    await I.fillField({css: '#dateCodicilAdded-year'}, grantOfProbateConfig.page1_codicilDate_year);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
