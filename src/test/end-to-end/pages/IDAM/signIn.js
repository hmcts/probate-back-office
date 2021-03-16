'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage('/');
    }

    let userName = useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser;
    let password = useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword;
    console.log(userName);
    console.log(password);

    await I.waitForText('Sign in');
    await I.fillField('#username', userName);
    await I.fillField('#password', password);

    await I.waitForNavigationToComplete('input[type="submit"]');
};
