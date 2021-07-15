'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser) {

    const I = this;
    await I.amOnLoadedPage('/');

    await I.waitForText('Sign in', 30);
    await I.waitForText('Email address', 30);
    await I.waitForText('Password', 30);

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword);

    await I.waitForNavigationToComplete('input[type="submit"]', 5);

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
    if (testConfig.TestForXUI) {
        await I.wait(2);
    }
};
