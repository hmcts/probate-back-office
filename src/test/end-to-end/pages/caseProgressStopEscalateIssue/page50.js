'use strict';

// Case worker sign out
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({css: 'select option[value="1: Object"]'});
    await I.waitForEnabled({css: '#sign-out'});
    await I.waitForNavigationToComplete('#sign-out');
};
