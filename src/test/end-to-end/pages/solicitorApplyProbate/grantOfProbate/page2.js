'use strict';

const grantOfProbateConfig = require('./grantOfProbate');
const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (verifyTrustCorpOpts, isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
    const I = this;
    await I.runAccessibilityTest();
    const dispNoticeLocator = {css: `#dispenseWithNotice_${grantOfProbateConfig.page2_dispenseWithNotice}`};
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayMedium);
    }

    if (isSolicitorNamedExecutor || isSolicitorApplyingExecutor) {
        await I.waitForText(grantOfProbateConfig.page2_prev_identified_execs_text);
        await I.waitForText(grantOfProbateConfig.page2_sol_name);
    } else {
        await I.dontSee(grantOfProbateConfig.page2_prev_identified_execs_text);
    }

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

    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.dontSeeElement({css: '#anyOtherApplyingPartners_Yes'});
    await I.dontSeeElement({css: '#otherPartnersApplyingAsExecutors'});

    await I.click({css: '#titleAndClearingType-TCTPartOthersRenouncing'});

    await I.scrollTo({css: '#anyOtherApplyingPartners_Yes'});
    await I.click({css: '#anyOtherApplyingPartners_Yes'});
    await I.waitForVisible({css: '#otherPartnersApplyingAsExecutors'});
    await I.click({css: '#anyOtherApplyingPartners_No'});
    await I.waitForInvisible({css: '#otherPartnersApplyingAsExecutors'});

    await I.scrollTo({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForElement('#trustCorpName');
    await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);
    await I.click(grantOfProbateConfig.page2_trustCorpPostcodeLink);
    await I.fillField('#trustCorpAddress__detailAddressLine1', grantOfProbateConfig.address_line1);
    await I.fillField('#trustCorpAddress__detailAddressLine2', grantOfProbateConfig.address_line2);
    await I.fillField('#trustCorpAddress__detailAddressLine3', grantOfProbateConfig.address_line3);
    await I.fillField('#trustCorpAddress__detailPostTown', grantOfProbateConfig.address_town);
    await I.fillField('#trustCorpAddress__detailCounty', grantOfProbateConfig.address_county);
    await I.fillField('#trustCorpAddress__detailPostCode', grantOfProbateConfig.address_postcode);
    await I.fillField('#trustCorpAddress__detailCountry', grantOfProbateConfig.address_country);

    await I.waitForText(grantOfProbateConfig.page2_waitForAnyOtherTcPartners);
    await I.click({css: '#anyOtherApplyingPartnersTrustCorp_Yes'});

    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.WaitForTextTimeout);
    await I.click('#additionalExecutorsTrustCorpList > div > button');
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayMedium);
    }

    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);

    await I.fillField('#probatePractitionersPositionInTrust', grantOfProbateConfig.page2_positionInTrust);
    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
