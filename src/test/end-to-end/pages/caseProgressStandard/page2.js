'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('./caseProgressConfig');

module.exports = async function () {
    const I = this;
    await I.waitForElement({css: 'form.check-your-answers'});

    await I.see(caseProgressConfig.page2_pageHeader, {css: 'h1'});
    await I.see(caseProgressConfig.page1_solFirmName);
    await I.see(caseProgressConfig.page1_solFirstname);
    await I.see(caseProgressConfig.page1_solSurname);
    await I.see(caseProgressConfig.page1_solAddr1);
    await I.waitForNavigationToComplete(commonConfig.continueButton);      
}
