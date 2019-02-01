'use strict';

const testConfig = require('src/test/config.js');
const useIdam = testConfig.TestUseIdam;

module.exports = function () {

    if (useIdam === 'true') {
        const I = this;

        I.amOnPage('/');
        I.see('Sign in');

/*
        I.fillField('username', process.env.testCitizenEmail);
        I.fillField('password', process.env.testCitizenPassword);
*/
        I.fillField('username', 'probatebackoffice@gmail.com');
        I.fillField('password', 'Monday01');

        I.waitForNavigationToComplete('input[value="Sign in"]');
    }
};
