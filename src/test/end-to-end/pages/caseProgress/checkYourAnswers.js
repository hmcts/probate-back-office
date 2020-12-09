'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function (htmlToCheck) {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert(formHtml); 
    if (htmlToCheck) {
        assert (I.htmlEquals(formHtml, htmlToCheck));
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);          
}
