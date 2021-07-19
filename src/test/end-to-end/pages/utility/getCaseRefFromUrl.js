'use strict';
const testConfig = require('src/test/config.js');

module.exports = async function (forXui = testConfig.TestForXUI) {
    const I = this;
    await I.testConfig.GetCaseRefFromUrlDelay;
    let url = await I.grabCurrentUrl();

    url = url.replace('#eventHistoryTab', '');
    return url.split('/').pop()
        .match(/.{4}/g)
        .join('-');
};
