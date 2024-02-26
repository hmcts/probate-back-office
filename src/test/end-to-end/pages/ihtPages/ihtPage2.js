'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
// Select No for HMRC Letter received
module.exports = async function (caseProgressConfig, optionValue) {
    const I = this;
    await I.waitForElement({css: `${caseProgressConfig.ihtHmrcLetter}_${optionValue}`});
    await I.click({css: `${caseProgressConfig.ihtHmrcLetter}_${optionValue}`});
    if (optionValue === 'Yes') {
        await I.waitForElement({css: `${caseProgressConfig.hmrcCodeTextBox}`});
        await I.fillField({css: `${caseProgressConfig.hmrcCodeTextBox}`}, caseProgressConfig.uniqueHmrcCode);
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
