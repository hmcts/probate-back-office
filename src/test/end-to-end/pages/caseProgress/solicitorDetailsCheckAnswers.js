'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function (caseProgressConfig, htmlToCheck) {
    const I = this;
    await I.waitForElement({css: 'form.check-your-answers'});

    if (caseProgressConfig.solDtlsPage2_pageHeader) {
        await I.see(caseProgressConfig.solDtlsPage2_pageHeader, {css: 'h1'});
    }
    await I.see(caseProgressConfig.solFirmName);
    await I.see(caseProgressConfig.solFirstname);
    await I.see(caseProgressConfig.solSurname);
    await I.see(caseProgressConfig.solAddr1);
    if (htmlToCheck) {
        const formHtml = await I.grabAttributeFrom('form', 'outerHTML');
        assert(I.htmlEquals(formHtml, htmlToCheck));
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
