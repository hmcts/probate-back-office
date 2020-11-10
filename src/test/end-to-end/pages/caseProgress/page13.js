'use strict';
const page13HtmlCheck = require('./page13-html-check');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');


// grant of probate details part 4
module.exports = async function () {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert (I.htmlEquals(formHtml, page13HtmlCheck.htmlCheck));
    await I.waitForNavigationToComplete(commonConfig.continueButton);          
}
