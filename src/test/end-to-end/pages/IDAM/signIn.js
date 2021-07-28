'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {

    const I = this;
    // const t = await I.addATabRetainingFocusOnOriginal();
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);
    // await I.removeTab(t);

    await I.waitForText('Sign in', 600);
    await I.waitForText('Email address');
    await I.waitForText('Password');

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvCwUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvCwPassword);

    await I.waitForNavigationToComplete('input[type="submit"]', testConfig.SignInDelay);
    await I.rejectCookies();
    await I.wait(signInDelay);
};
