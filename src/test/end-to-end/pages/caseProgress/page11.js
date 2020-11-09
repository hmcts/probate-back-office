const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// grant of probate details part 2
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('#otherExecutorExists-No'); 
    await I.click('#otherExecutorExists-No');
    await I.waitForNavigationToComplete(commonConfig.continueButton);    
};
