'use strict';
const testConfig = require('src/test/config.js');

module.exports = async function (forXui = testConfig.TestForXUI) {
    const I = this;
    let url = await I.grabCurrentUrl();
    let caseRef;
    if (forXui) {
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
