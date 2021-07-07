'use strict';

const testConfig = require('src/test/config');
const newCaseConfig = require('./newCaseConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(newCaseConfig.waitForText, testConfig.TestTimeToWaitForText);
    const numVisibleCookieBannerEls = await I.grabNumberOfVisibleElements({css: 'body exui-root xuilib-cookie-banner'});
    if (numVisibleCookieBannerEls > 0) {
        //check to see we can still click
        const bannerButton = await I.grabNumberOfVisibleElements({css: 'button.govuk-button[value="reject"]'});
        if (bannerButton > 0) {
            // just reject additional cookies
            const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
            await I.waitForEnabled(rejectLocator);
            await I.click(rejectLocator);
        }
    }
    await I.waitForEnabled({css: testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator});
    await I.waitForNavigationToComplete(testConfig.TestForXUI ? newCaseConfig.xuiCreateCaseLocator : newCaseConfig.ccduilCreateCaselocator);
    if (testConfig.TestForXUI) {
        await I.wait(0.5);
    }
};
