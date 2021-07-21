'use strict';
const testConfig = require('src/test/config');

module.exports = async function() {
    if (testConfig.RejectCookies) {
        try {
            const I = this;
            const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
            const numVisibleCookieBannerEls = await I.grabNumberOfVisibleElements(rejectLocator);
            if (numVisibleCookieBannerEls > 0) {
                // just reject additional cookies
                await I.waitForEnabled(rejectLocator);
                await I.click(rejectLocator);
                await I.wait(testConfig.RejectCookieDelay);
            }
        } catch (e) {
            console.error(`error trying to close cookie banner: ${e.message}`);
        }
    }
};
