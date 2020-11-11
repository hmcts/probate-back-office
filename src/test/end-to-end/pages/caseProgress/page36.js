'use strict';

// CW case details - sign out
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails    
    await I.waitForElement({css: 'select option[value="1: Object"]'});
    await I.waitForElement({css: 'div.proposition-right a'});
    await I.click('div.proposition-right a');
    await I.waitForNavigationToComplete();   
};
