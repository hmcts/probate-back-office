'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function () {
    const I = this;
    await I.waitForElement('form');

    await I.see('Apply for probate', {css: 'h1'});
    await I.see('DummySolicitor');
    await I.see('Billy');
    await I.see('Bloggs');
    await I.see('1 Main St');

    await I.waitForNavigationToComplete(commonConfig.continueButton);      
}
