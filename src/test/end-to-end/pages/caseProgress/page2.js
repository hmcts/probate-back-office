const page2HtmlCheck = require('./page2-html-check');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function () {
    const I = this;
    await I.waitForElement('form');
    const formHtml = await I.grabAttributeFrom('form', 'outerHTML');
    assert (formHtml === page2HtmlCheck.htmlCheck);
    await I.waitForNavigationToComplete(commonConfig.continueButton);      
}
