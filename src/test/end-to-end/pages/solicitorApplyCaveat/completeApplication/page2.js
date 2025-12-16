'use strict';

const testConfig = require('src/test/config.cjs');
const completeApplicationConfig = require('./completeApplication');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const dateFns = require('date-fns');
const {legacyParse, convertTokens} = require('@date-fns/upgrade/v2');
//const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/caseDetailsTabConfig');
// const click = require("webdriverio/build/commands/element/click");

module.exports = async function (caseRef) {
    const I = this;
    completeApplicationConfig.page2_notification_date = dateFns.format(legacyParse(new Date()), convertTokens('DD/MM/YYYY'));
    await I.waitForText(completeApplicationConfig.page2_waitForText, testConfig.WaitForTextTimeout);
    await I.runAccessibilityTest();
    await I.see(completeApplicationConfig.page2_waitForText);
    await I.see(caseRef);
    await I.see(completeApplicationConfig.page2_confirmationText);
    await I.see(completeApplicationConfig.page2_app_ref);
    await I.see(completeApplicationConfig.page2_notification_date_text);
    await I.see(completeApplicationConfig.page2_notification_date);

    await I.waitForNavigationToComplete(commonConfig.submitButton);
};
