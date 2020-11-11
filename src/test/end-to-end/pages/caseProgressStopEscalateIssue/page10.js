'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 1
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#willAccessOriginal-Yes'); 
    await I.click('#willAccessOriginal-Yes');
    await I.waitForElement('#willHasCodicils-No'); 
    await I.click('#willHasCodicils-No');
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
