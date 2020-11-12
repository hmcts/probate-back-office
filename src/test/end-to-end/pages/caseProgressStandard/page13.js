'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const assert = require('assert');


// grant of probate details part 4
module.exports = async function () {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert(formHtml); // similar html already asserted in caseProgressStopEscalateIssue
    await I.waitForNavigationToComplete(commonConfig.continueButton);          
}
