'use strict';

const testConfig = require('src/test/config.js');
const registrarsDecisionConfig = require('./registrarsDecisionConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef) {
    const I = this;
    await I.waitForText(registrarsDecisionConfig.waitForText, testConfig.WaitForTextTimeout);
    await I.see(caseRef);
    await I.waitForEnabled({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
    await I.dontSeeCheckboxIsChecked({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
    await I.click({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
    await I.fillField('#registrarDirectionToAdd_furtherInformation', registrarsDecisionConfig.furtherInformation);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
