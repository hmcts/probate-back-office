'use strict';

const testConfig = require('src/test/config.js');

module.exports = function () {

    const I = this;

    I.amOnPage('/');
    I.see('Sign in');

    // I.fillField('username', testConfig.TestEnvUser);
    // I.fillField('password', testConfig.TestEnvPassword);

    I.fillField('username', 'douglasrice1969+cw@gmail.com');
    I.fillField('password', 'Probate123');

    I.waitForNavigationToComplete('input[value="Sign in"]');
};
