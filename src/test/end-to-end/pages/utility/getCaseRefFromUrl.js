'use strict';

module.exports = async function () {
    const I = this;
    await I.wait(3);
    let url = await I.grabCurrentUrl();
    await I.wait(1);
    url = url.replace('#Event%20History', '');
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');
    return caseRef;
};
