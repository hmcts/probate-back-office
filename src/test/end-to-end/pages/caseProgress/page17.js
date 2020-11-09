const caseProgressConfig = require('./caseProgressConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 8 - statement of truth
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('solsSOTJobTitle');
    await I.fillField('#solsSOTJobTitle', caseProgressConfig.page17_JobTitle);
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
