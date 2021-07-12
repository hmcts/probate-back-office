'use strict';

const applyProbateConfig = require('./applyProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
    const I = this;
    await I.waitForElement('#solsApplyPage');
    await I.runAccessibilityTest();
    await I.waitForText(applyProbateConfig.page2_subheading);
    await I.waitForText(applyProbateConfig.page2_probatePractionerHelp);

    await I.waitForElement(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
    await I.click(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
    await I.fillField('#solsForenames', applyProbateConfig.page2_sol_forename);
    await I.fillField('#solsSurname', applyProbateConfig.page2_sol_surname);

    await I.fillField('#solsSOTForenames', applyProbateConfig.page2_sol_forename);
    await I.fillField('#solsSOTSurname', applyProbateConfig.page2_sol_surname);

    if (isSolicitorNamedExecutor) {
        await I.click({css: '#solsSolicitorIsExec_Yes'});
        await I.waitForVisible({css: '#applyForProbatePageHint1'});

        if (isSolicitorApplyingExecutor) {
            await I.click({css: '#solsSolicitorIsApplying_Yes'});
            await I.waitForVisible({css: '#applyForProbatePageHint1'});
        } else {
            await I.click({css: '#solsSolicitorIsApplying_No'});
            await I.waitForVisible({css: '#solsSolicitorNotApplyingReason-PowerReserved'});
            await I.click({css: '#solsSolicitorNotApplyingReason-PowerReserved'});
        }
    } else {
        await I.click({css: '#solsSolicitorIsExec_No'});
        await I.click({css: `#solsSolicitorIsApplying_${isSolicitorApplyingExecutor ? 'Yes' : 'No'}`});
        if (isSolicitorApplyingExecutor) {
            await I.waitForVisible({css: '#applyForProbatePageHint2'});
        }
    }

    await I.fillField('#solsSolicitorFirmName', applyProbateConfig.page2_firm_name);

    await I.click(applyProbateConfig.UKpostcodeLink);
    await I.fillField('#solsSolicitorAddress__detailAddressLine1', applyProbateConfig.address_line1);
    await I.fillField('#solsSolicitorAddress__detailAddressLine2', applyProbateConfig.address_line2);
    await I.fillField('#solsSolicitorAddress__detailAddressLine3', applyProbateConfig.address_line3);
    await I.fillField('#solsSolicitorAddress__detailPostTown', applyProbateConfig.address_town);
    await I.fillField('#solsSolicitorAddress__detailCounty', applyProbateConfig.address_county);
    await I.fillField('#solsSolicitorAddress__detailPostCode', applyProbateConfig.address_postcode);
    await I.fillField('#solsSolicitorAddress__detailCountry', applyProbateConfig.address_country);

    await I.fillField('#solsSolicitorEmail', applyProbateConfig.page2_sol_email);
    await I.fillField('#solsSolicitorPhoneNumber', applyProbateConfig.page2_phone_num);
    await I.fillField('#solsSolicitorAppReference', applyProbateConfig.page2_app_ref);

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
