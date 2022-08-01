'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault) {

    const I = this;
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);

    await I.waitForText('Sign in', 600);
    await I.waitForText('Email address');
    await I.waitForText('Password');

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvSACUser1 : testConfig.TestEnvSACUser2);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvSACPassword1 : testConfig.TestEnvSACPassword2);


    await I.waitForNavigationToComplete('input[type="submit"]', signInDelay);
    await I.dontSee({css: '#username'});
    await I.rejectCookies();
    await I.wait(signInDelay);
};
