'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, documentRemoveConfig, uploadNumber=1) {

    const I = this;
    I.waitForText(documentRemoveConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    let i;
    for (i = uploadNumber; i > 0; i--) {
        I.click({type: 'button'}, `${documentRemoveConfig.id}>div>div>div:nth-child(${i+1})>div>div.float-right`);
        I.waitForText(documentRemoveConfig.waitForText2);
        I.click('Remove', {css: 'ccd-remove-dialog'});
    }

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
