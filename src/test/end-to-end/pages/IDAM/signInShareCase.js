'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {

    const I = this;
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);

    await I.waitForText('Sign in', 600);
    await I.waitForText('Email address');
    await I.waitForText('Password');

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvProfUser1);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvProfPassword1);


    await I.waitForNavigationToComplete('input[type="submit"]', signInDelay);
    await I.dontSee({css: '#username'});
    await I.rejectCookies();
    await I.wait(signInDelay);
};
