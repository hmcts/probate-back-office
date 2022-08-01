'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, signInDelay = testConfig.SignInDelayDefault, SAC =false) {

    const I = this;
    await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/`);
    await I.wait(testConfig.ManualDelayMedium);

    await I.waitForText('Sign in', 600);
    await I.waitForText('Email address');
    await I.waitForText('Password');
    // testConfig.TestEnvCwUser
    if (useProfessionalUser == false){
        await I.fillField('#username', testConfig.TestEnvProfUser);
        await I.fillField('#password', testConfig.TestEnvProfPassword);
    }
    else if (SAC == true) {
        await I.fillField('#username', testConfig.TestEnvSACUser);
        await I.fillField('#password', testConfig.TestEnvSACPassword1);
    }
    else {
        await I.fillField('#username', testConfig.TestEnvSACUser);
        await I.fillField('#password', testConfig.TestEnvSACPassword1);
    }


    await I.waitForNavigationToComplete('input[type="submit"]', signInDelay);
    await I.dontSee({css: '#username'});
    await I.rejectCookies();
    await I.wait(signInDelay);
};
