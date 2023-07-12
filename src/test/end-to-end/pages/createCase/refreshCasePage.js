'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;

    await I.authenticateWithIdamIfAvailable(true, testConfig.CaseProgressSignInDelay);
    await I.selectNewCase();
};
