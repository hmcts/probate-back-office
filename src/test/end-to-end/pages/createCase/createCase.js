'use strict';

const testConfig = require('src/test/config.js');
const createCaseConfig = require('./createCaseConfig');

module.exports = async function (jurisdiction, caseType, event) {

    const I = this;
    await I.waitForText(createCaseConfig.waitForText, testConfig.TestTimeToWaitForText || 60);
    //In saucelabs this page is not able to load so waiting for more time
    if (testConfig.TestForXUI) {
        await I.wait(180);

    }
    if (testConfig.TestForCrossBrowser) {
        await I.wait(5);
    }
    await I.waitForEnabled({css: '#cc-jurisdiction'}, );
    await I.retry(5).selectOption('#cc-jurisdiction', jurisdiction);
    await I.waitForEnabled({css: '#cc-case-type'}, 120);
    await I.retry(5).selectOption('#cc-case-type', caseType);
    await I.waitForEnabled({css: '#cc-event'}, 120);
    await I.retry(5).selectOption('#cc-event', event);

    await I.waitForEnabled(createCaseConfig.startButton, 120);
    await I.waitForNavigationToComplete(createCaseConfig.startButton);
};
