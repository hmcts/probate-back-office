'use strict';

// CW case details - sign out
module.exports = async function () {
    const I = this;
    try {
        // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
        await I.waitForElement({css: '#sign-out'});
        await I.click({css: '#sign-out'});
        await I.waitForNavigationToComplete();

    } catch (e) {
        return Promise.reject(e);
    }
};
