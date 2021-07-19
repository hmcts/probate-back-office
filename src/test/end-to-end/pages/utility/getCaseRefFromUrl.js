'use strict';
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    await I.wait(testConfig.GetCaseRefFromUrlDelay);
    let url = await I.grabCurrentUrl();

    return url.replace('#Event%20History', '').split('/').pop()
        .match(/.{4}/g)
        .join('-');
};
