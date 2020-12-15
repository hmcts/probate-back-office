'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage('/');
        // await I.waitForNavigationToComplete();    
    }

    const textToWaitFor = useProfessionalUser ? 'Sign in or create an account' : 'Sign in';
    await I.waitForText(textToWaitFor);
    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword);

    await I.waitForNavigationToComplete('input[type="submit"]');
};
