'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {

    const I = this;
    I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);

    await I.waitForText('Sign in', 60);
    await I.waitForText('Email address');
    await I.waitForText('Password');

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvCwUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvCwPassword);

    await I.waitForNavigationToComplete('input[type="submit"]', signInDelay);
    await I.dontSee({css: '#username'});
    await I.rejectCookies();
    await I.wait(signInDelay);
};
