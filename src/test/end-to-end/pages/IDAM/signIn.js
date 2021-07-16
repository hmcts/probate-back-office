'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, cookieRejectDelay = testConfig.CookieRejectDelayDefault) {

    const I = this;
    await I.amOnLoadedPage(useProfessionalUser ? `${testConfig.TestXuiUrl}/` : `${testConfig.TestCcdUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);

    await I.waitForText('Sign in', 240000);

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvCwUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvCwPassword);

    await I.waitForNavigationToComplete('input[type="submit"]', testConfig.SignInDelay);

    if (testConfig.RejectCookies) {
        const numVisibleCookieBannerEls = await I.grabNumberOfVisibleElements({css: 'body exui-root xuilib-cookie-banner'});
        if (numVisibleCookieBannerEls > 0) {
            //check to see we can still click
            const bannerButton = await I.grabNumberOfVisibleElements({css: 'button.govuk-button[value="reject"]'});
            if (bannerButton > 0) {
                // just reject additional cookies
                const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
                await I.waitForEnabled(rejectLocator);
                await I.click(rejectLocator);
                await I.wait(cookieRejectDelay);
            }
        }    
    }
};
