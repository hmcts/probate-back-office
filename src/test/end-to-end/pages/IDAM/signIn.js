'use strict';

const testConfig = require('src/test/config.js');

module.exports = function () {

    const I = this;

    I.amOnPage('/');
    I.see('Sign in');

    I.fillField('username', testConfig.TestEnvUser);
    I.fillField('password', testConfig.TestEnvPassword);

    I.waitForNavigationToComplete('input[value="Sign in"]');
};
