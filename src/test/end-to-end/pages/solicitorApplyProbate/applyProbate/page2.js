'use strict';

const applyProbateConfig = require('./applyProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (isSolicitorExecutor = false, isSolicitorMainApplicant = false) {
    const I = this;
    await I.waitForElement('#solsApplyPage');
    await I.runAccessibilityTest();

    await I.click(`#solsSolicitorWillSignSOT-${applyProbateConfig.page2_optionNo}`);
    await I.fillField('#solsForenames', applyProbateConfig.page2_sol_forename);
    await I.fillField('#solsSurname', applyProbateConfig.page2_sol_surname);

    await I.fillField('#solsSOTForenames', applyProbateConfig.page2_sol_forename);
    await I.fillField('#solsSOTSurname', applyProbateConfig.page2_sol_surname);

    if (isSolicitorExecutor) {
        await I.click(`#solsSolicitorIsExec-${applyProbateConfig.page2_optionYes}`);

        if (isSolicitorMainApplicant) {
            await I.click(`#solsSolicitorIsApplying-${applyProbateConfig.page2_optionYes}`);
        } else {
            await I.click(`#solsSolicitorIsApplying-${applyProbateConfig.page2_optionNo}`);
            await I.click(`#solsSolicitorIsApplying-${solsSolicitorNotApplyingReason-PowerReserved}`);
        }
    } else {
        await I.click(`#solsSolicitorIsExec-${applyProbateConfig.page2_optionNo}`);
    }

    await I.fillField('#solsSolicitorFirmName', applyProbateConfig.page2_firm_name);

    await I.click(applyProbateConfig.UKpostcodeLink);
    await I.fillField('#solsSolicitorAddress_AddressLine1', applyProbateConfig.address_line1);
    await I.fillField('#solsSolicitorAddress_AddressLine2', applyProbateConfig.address_line2);
    await I.fillField('#solsSolicitorAddress_AddressLine3', applyProbateConfig.address_line3);
    await I.fillField('#solsSolicitorAddress_PostTown', applyProbateConfig.address_town);
    await I.fillField('#solsSolicitorAddress_County', applyProbateConfig.address_county);
    await I.fillField('#solsSolicitorAddress_PostCode', applyProbateConfig.address_postcode);
    await I.fillField('#solsSolicitorAddress_Country', applyProbateConfig.address_country);

    await I.fillField('#solsSolicitorEmail', applyProbateConfig.page2_sol_email);
    await I.fillField('#solsSolicitorPhoneNumber', applyProbateConfig.page2_phone_num);
    await I.fillField('#solsSolicitorAppReference', applyProbateConfig.page2_app_ref);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
