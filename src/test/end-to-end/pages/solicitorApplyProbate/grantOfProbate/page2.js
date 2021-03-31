'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (verifyTrustCorpOpts) {
    const I = this;
    const dispNoticeLocator = {css: `#dispenseWithNotice-${grantOfProbateConfig.optionYes}`};
    await I.waitForElement(dispNoticeLocator);
    await I.runAccessibilityTest();
    await I.scrollTo(dispNoticeLocator);
    await I.waitForClickable(dispNoticeLocator);
    await I.click(dispNoticeLocator);
    if (verifyTrustCorpOpts) {
        await I.verifyTitleAndClearingTypeOptions();
    } else {
        await I.scrollTo('#titleAndClearingType-TCTNoT');
    }

    await I.waitForClickable({css: '#titleAndClearingType-TCTNoT'});
    await I.click({css: '#titleAndClearingType-TCTNoT'});
    await I.fillField('#titleAndClearingTypeNoT', grantOfProbateConfig.page2_titleAndClearingTypeNoT);

    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForElement('#trustCorpName');
    await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);
    await I.click(grantOfProbateConfig.UKpostcodeLink);
    await I.fillField('#trustCorpAddress_AddressLine1', grantOfProbateConfig.address_line1);
    await I.fillField('#trustCorpAddress_AddressLine2', grantOfProbateConfig.address_line2);
    await I.fillField('#trustCorpAddress_AddressLine3', grantOfProbateConfig.address_line3);
    await I.fillField('#trustCorpAddress_PostTown', grantOfProbateConfig.address_town);
    await I.fillField('#trustCorpAddress_County', grantOfProbateConfig.address_county);
    await I.fillField('#trustCorpAddress_PostCode', grantOfProbateConfig.address_postcode);
    await I.fillField('#trustCorpAddress_Country', grantOfProbateConfig.address_country);

    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.TestTimeToWaitForText);
    await I.click('#additionalExecutorsTrustCorpList > div > button');
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25);
    }

    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);

    await I.fillField('#lodgementAddress', grantOfProbateConfig.page2_lodgementAddress);
    await I.fillField('#lodgementDate-day', grantOfProbateConfig.page2_lodgementDate_day);
    await I.fillField('#lodgementDate-month', grantOfProbateConfig.page2_lodgementDate_month);
    await I.fillField('#lodgementDate-year', grantOfProbateConfig.page2_lodgementDate_year);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
