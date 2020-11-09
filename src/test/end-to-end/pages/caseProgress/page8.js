const page8HtmlCheck = require('./page8-html-check');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function () {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert (formHtml === page8HtmlCheck.htmlCheck);
    await I.waitForNavigationToComplete(commonConfig.continueButton);          
}
