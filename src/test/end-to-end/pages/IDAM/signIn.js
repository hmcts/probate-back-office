'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (useProfessionalUser, isAlreadyAtSignOnPage) {

    const I = this;
    if (!isAlreadyAtSignOnPage) {
        await I.amOnLoadedPage(useProfessionalUser ? `${testConfig.TestXuiUrl}/` : `${testConfig.TestCcdUrl}/`);
    }

    await I.waitForText('Sign in', 240000);

    await I.fillField('#username', useProfessionalUser ? testConfig.TestEnvProfUser : testConfig.TestEnvCwUser);
    await I.fillField('#password', useProfessionalUser ? testConfig.TestEnvProfPassword : testConfig.TestEnvCwPassword);

    await I.waitForNavigationToComplete('input[type="submit"]', useProfessionalUser);
};
