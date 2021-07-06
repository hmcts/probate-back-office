'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');

module.exports = async function () {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert (formHtml.includes('Barney'));
    assert (formHtml.includes('Rubble'));
    assert (formHtml.includes('10 Oct 2020'));
    assert (formHtml.includes('10 Oct 1967'));
    assert (formHtml.includes('2 The High St'));
    assert (formHtml.includes('Swindon'));
    assert (formHtml.includes('SN15JU'));
    assert (formHtml.includes('UK'));
    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
};
