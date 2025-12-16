'use strict';
const testConfig = require('src/test/config.cjs');

module.exports = async function () {
    const I = this;
    await I.wait(testConfig.GetCaseRefFromUrlDelay);
    const url = await I.grabCurrentUrl();

    return url
        .replace('#Event%20History', '')
        .replace('#Case%20Progress', '')
        .split('/')
        .pop()
        .match(/.{4}/g)
        .join('-');
};
