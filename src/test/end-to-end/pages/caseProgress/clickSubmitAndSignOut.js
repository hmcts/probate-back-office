'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const testConfig = require('src/test/config.js');

// CW event summary and description and final confirm case printed
module.exports = async function () {
    const I = this;

    await I.wait(testConfig.CaseProgressClickGoButtonInitialDelay);
    await I.waitForElement({css: '#field-trigger-summary'});
    await I.waitForEnabled({css: commonConfig.submitButton});
    await I.waitForNavigationToComplete(commonConfig.submitButton);
    await I.signOut();
};
