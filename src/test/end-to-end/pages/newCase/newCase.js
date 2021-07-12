'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function (forXui = testConfig.TestForXUI) {

    const I = this;

    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);

    /*
    Rejecting cookies interferes with ExUi and causes a crash (currently)
    try {
        const numVisibleCookieBannerEls = await I.grabNumberOfVisibleElements({css: 'xuilib-cookie-banner'});
        if (numVisibleCookieBannerEls > 0) {
            // just reject additional cookies
            const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
            await I.waitForEnabled(rejectLocator);
            await I.click(rejectLocator);
            if (testConfig.TestForXUI) {
                await I.wait(testConfig.ManualDelayMedium);
            }
        }
    } catch (e) {
        console.error(`error trying to close cookie banner: ${e.message}`);
    }
    */
    const locator = forXui ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduiCreateCaseLocator;
    await I.waitForEnabled({css: locator});
    await I.waitForNavigationToComplete(locator, forXui);
};
