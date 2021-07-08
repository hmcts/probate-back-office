'use strict';

const applyProbateConfig = require('./applyProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (isSolicitorExecutor = false, isSolicitorMainApplicant = false) {
    const I = this;
    await I.waitForElement('#solsSolicitorFirmName');
    await I.runAccessibilityTest();

    await I.fillField('#solsSolicitorFirmName', applyProbateConfig.page2_firm_name);

    if (isSolicitorExecutor) {
        await I.click(`#solsSolicitorIsExec_${applyProbateConfig.page2_optionYes}`);
        await I.fillField('#solsSOTForenames', applyProbateConfig.page2_sol_forename);
        await I.fillField('#solsSOTSurname', applyProbateConfig.page2_sol_surname);
        if (isSolicitorMainApplicant) {
            await I.click(`#solsSolicitorIsMainApplicant_${applyProbateConfig.page2_optionYes}`);
        } else {
            await I.click(`#solsSolicitorIsMainApplicant_${applyProbateConfig.page2_optionNo}`);
            await I.click(`#solsSolicitorIsApplying_${applyProbateConfig.page2_optionYes}`);
        }
    } else {
        await I.click(`#solsSolicitorIsExec_${applyProbateConfig.page2_optionNo}`);
    }

    await I.click(applyProbateConfig.UKpostcodeLink);
    await I.fillField('#solsSolicitorAddress__detailAddressLine1', applyProbateConfig.address_line1);
    await I.fillField('#solsSolicitorAddress__detailAddressLine2', applyProbateConfig.address_line2);
    await I.fillField('#solsSolicitorAddress__detailAddressLine3', applyProbateConfig.address_line3);
    await I.fillField('#solsSolicitorAddress__detailPostTown', applyProbateConfig.address_town);
    await I.fillField('#solsSolicitorAddress__detailCounty', applyProbateConfig.address_county);
    await I.fillField('#solsSolicitorAddress__detailPostCode', applyProbateConfig.address_postcode);
    await I.fillField('#solsSolicitorAddress__detailCountry', applyProbateConfig.address_country);

    await I.fillField('#solsSolicitorAppReference', applyProbateConfig.page2_app_ref);
    await I.fillField('#solsSolicitorEmail', applyProbateConfig.page2_sol_email);
    await I.fillField('#solsSolicitorPhoneNumber', applyProbateConfig.page2_phone_num);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
