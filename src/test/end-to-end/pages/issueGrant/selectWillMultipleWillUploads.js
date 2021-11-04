'use strict';

const testConfig = require('src/test/config.js');
const issueGrantConfig = require('./issueGrantConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports =  async function (caseRef) {

    const I = this;

        await I.waitForText(issueGrantConfig.waitForText, testConfig.WaitForTextTimeout);

        await I.see(caseRef);

        await I.waitForElement({css: `#willSelection_0_documentSelected-${issueGrantConfig.willSelection}`});
        await I.waitForEnabled({css: `#willSelection_0_documentSelected-${issueGrantConfig.willSelection}`});
        await I.checkOption({css: `#willSelection_0_documentSelected-${issueGrantConfig.willSelection}`});

        await I.waitForNavigationToComplete(commonConfig.continueButton);
}
