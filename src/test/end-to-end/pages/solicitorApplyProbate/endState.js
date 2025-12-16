'use strict';

const testConfig = require('src/test/config.cjs');

module.exports = async function (endState) {
    const I = this;
    await I.waitForText('Event History');
    await I.clickTab('Event History', true);
    await I.waitForText(endState, testConfig.WaitForTextTimeout);
};
