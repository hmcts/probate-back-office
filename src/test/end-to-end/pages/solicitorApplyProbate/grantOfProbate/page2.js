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
    await I.waitForElement('#titleAndClearingType-TCTNoT', 40);
    if (verifyTrustCorpOpts) {
        await I.verifyTitleAndClearingTypeOptionsPage();
    } else {
        await I.wait(2);
        await I.scrollTo({css: '#titleAndClearingType-TCTNoT'});
    }

    await I.logInfo("In Title page1");
    await I.waitForClickable({css: '#titleAndClearingType-TCTNoT'});
    await I.logInfo("In Title page2");
    await I.click({css: '#titleAndClearingType-TCTNoT'});

    await I.logInfo("In Title page3");
    await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.logInfo("In Title page4");
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.logInfo("In Title page5");
    await I.dontSeeElement({css: '#anyOtherApplyingPartners_Yes'});
    await I.logInfo("In Title page6");
    await I.dontSeeElement({css: '#otherPartnersApplyingAsExecutors'});

    await I.logInfo("In Title page7");
    await I.click({css: '#titleAndClearingType-TCTPartOthersRenouncing'});

    await I.logInfo("In Title page8");
    await I.scrollTo({css: '#anyOtherApplyingPartners_Yes'});
    await I.logInfo("In Title page9");
    await I.click({css: '#anyOtherApplyingPartners_Yes'});
    await I.logInfo("In Title page10");
    await I.waitForVisible({css: '#otherPartnersApplyingAsExecutors'});
    await I.logInfo("In Title page11");
    await I.click({css: '#anyOtherApplyingPartners_No'});
    await I.logInfo("In Title page12");
    await I.waitForInvisible({css: '#otherPartnersApplyingAsExecutors'});

    await I.logInfo("In Title page13");
    await I.scrollTo({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.logInfo("In Title page14");
    await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.logInfo("In Title page15");
    await I.waitForElement('#trustCorpName');
    await I.logInfo("In Title page16");
    await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);
    await I.logInfo("In Title page17");
    await I.click(grantOfProbateConfig.page2_trustCorpPostcodeLink);
    await I.logInfo("In Title page18");
    await I.fillField('#trustCorpAddress__detailAddressLine1', grantOfProbateConfig.address_line1);
    await I.logInfo("In Title page19");
    await I.fillField('#trustCorpAddress__detailAddressLine2', grantOfProbateConfig.address_line2);
    await I.logInfo("In Title page20");
    await I.fillField('#trustCorpAddress__detailAddressLine3', grantOfProbateConfig.address_line3);
    await I.logInfo("In Title page21");
    await I.fillField('#trustCorpAddress__detailPostTown', grantOfProbateConfig.address_town);
    await I.logInfo("In Title page22");
    await I.fillField('#trustCorpAddress__detailCounty', grantOfProbateConfig.address_county);
    await I.logInfo("In Title page23");
    await I.fillField('#trustCorpAddress__detailPostCode', grantOfProbateConfig.address_postcode);
    await I.logInfo("In Title page24");
    await I.fillField('#trustCorpAddress__detailCountry', grantOfProbateConfig.address_country);

    await I.logInfo("In Title page25");
    await I.waitForText(grantOfProbateConfig.page2_waitForAnyOtherTcPartners);
    await I.logInfo("In Title page26");
    await I.click({css: '#anyOtherApplyingPartnersTrustCorp_Yes'});

    await I.logInfo("In Title page27");
    await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.WaitForTextTimeout);
    await I.logInfo("In Title page28");
    await I.click('#additionalExecutorsTrustCorpList > div > button');
    await I.logInfo("In Title page29");
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayMedium);
    }

    await I.logInfo("In Title page30");
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    await I.logInfo("In Title page31");
    await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);

    await I.logInfo("In Title page32");
    await I.fillField('#probatePractitionersPositionInTrust', grantOfProbateConfig.page2_positionInTrust);
    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
