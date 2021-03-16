'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage('/');
    }

    const userName = useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser;
    const password = useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword;
    /* eslint-disable no-console */

    console.log(userName);
    console.log(password);
    /* eslint-disable no-console */

    await I.waitForText('Sign in');
    await I.fillField('#username', userName);
    await I.fillField('#password', password);

    await I.waitForNavigationToComplete('input[type="submit"]');
};
