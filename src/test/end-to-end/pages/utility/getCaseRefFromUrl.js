'use strict';
const testConfig = require('src/test/config.js');

module.exports = async function () {
    const I = this;
    let url = await I.grabCurrentUrl();
    let caseRef;
    if (testConfig.TestForXUI) {
        await I.wait(2);
    }
    if (testConfig.TestForXUI) {
        url = url.replace('#Event%20History', '');
        caseRef = url.split('/').pop()
            .match(/.{4}/g)
            .join('-');

    } else {
        url = url.replace('#eventHistoryTab', '');
        caseRef = url.split('/').pop()
            .match(/.{4}/g)
            .join('-');
    }

    return caseRef;
};
