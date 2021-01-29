'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage('/');
    }

    await I.waitForText('Sign in');

    const un = testConfig.TestEnvProfUser.substr(0, 3);
    const x = process.env.PROF_USER_EMAIL.substr(0, 3);

    /* eslint-disable no-console */
    console.info (`1 = ${un}`);
    console.info (`2 = ${x}`);

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword);

    await I.waitForNavigationToComplete('input[type="submit"]');
};
