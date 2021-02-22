'use strict';

module.exports = async function () {
    const I = this;
    let url = await I.grabCurrentUrl();
    url = url.replace('#eventHistoryTab', '');
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');
    return caseRef;
};
