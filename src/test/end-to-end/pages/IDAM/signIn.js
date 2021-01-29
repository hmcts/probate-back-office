'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage('/');
    }

    await I.waitForText('Sign in');

    /* eslint-disable no-console */
    console.info (`useProfessionalUser = ${useProfessionalUser}`);
    console.info (`username = ${useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser}`);
    console.info (`prof user env var = ${process.env.PROF_USER_EMAIL}`);

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvPassword);

    await I.waitForNavigationToComplete('input[type="submit"]');
};
