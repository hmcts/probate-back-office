'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    await I.waitForClickable({css: `#dispenseWithNotice-${grantOfProbateConfig.optionYes}`});
    await I.runAccessibilityTest();
    await I.click(`#dispenseWithNotice-${grantOfProbateConfig.optionYes}`);
    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForElement('#trustCorpName');
    await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);
    await I.fillField('#actingTrustCorpName', grantOfProbateConfig.page2_executorFirstName);
    await I.fillField('#positionInTrustCorp', grantOfProbateConfig.page2_positionInTrustCorp);

    await I.click(`#additionalExecutorsTrustCorp-${grantOfProbateConfig.optionYes}`);
    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.TestTimeToWaitForText);
    await I.click('#additionalExecutorsTrustCorpList > div > button');
    await I.fillField('#additionalExecutorsTrustCorpList_0_otherActingForTrustCorpName', grantOfProbateConfig.page2_executorFirstName);
    await I.fillField('#additionalExecutorsTrustCorpList_0_otherActingForTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);

    await I.fillField('#lodgementAddress', grantOfProbateConfig.page2_lodgementAddress);
    await I.fillField('#lodgementAddress', grantOfProbateConfig.page2_lodgementAddress);
    await I.fillField('#lodgementDate-day', grantOfProbateConfig.page2_lodgementDate_day);
    await I.fillField('#lodgementDate-month', grantOfProbateConfig.page2_lodgementDate_month);
    await I.fillField('#lodgementDate-year', grantOfProbateConfig.page2_lodgementDate_year);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
