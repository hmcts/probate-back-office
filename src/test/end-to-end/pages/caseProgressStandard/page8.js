'use strict';
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('./caseProgressConfig');
const assert = require('assert');

module.exports = async function () {
    const I = this;
    await I.waitForElement('form.check-your-answers');
    const formHtml = await I.grabAttributeFrom('form.check-your-answers', 'outerHTML');
    assert (formHtml.includes(caseProgressConfig.page4_deceasedFirstname));
    assert (formHtml.includes(caseProgressConfig.page4_deceasedSurname));
    assert (formHtml.includes('11 Nov 2020'));
    assert (formHtml.includes('9 Sep 1978'));
    assert (formHtml.includes(caseProgressConfig.page5_deceasedAddr1));
    assert (formHtml.includes(caseProgressConfig.page5_deceasedAddrTown));
    assert (formHtml.includes(caseProgressConfig.page5_deceasedAddrPostcode));
    assert (formHtml.includes(caseProgressConfig.page5_deceasedAddrCountry));
    await I.waitForNavigationToComplete(commonConfig.continueButton);          
}
