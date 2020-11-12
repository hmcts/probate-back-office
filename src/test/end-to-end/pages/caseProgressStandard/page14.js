'use strict';
const assert = require('assert');

// grant of probate details part 5
module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('a[aria-controls="caseProgressTab"][aria-selected=true]'); 

    // Check text on lhs side is all correct.
    const texts = await I.grabTextFrom('markdown  p.govuk-body-s');
    assert (texts.length === 17);

    const linkLocator = {css: 'p.govuk-body-s a.govuk-link'};
    const linkText = await I.grabTextFrom(linkLocator);
    assert (linkText === 'Review and sign legal statement and submit application'); 

    await I.seeNumberOfVisibleElements('p.govuk-body-s a', 1);

    const link = await I.grabAttributeFrom(linkLocator, 'href');
    assert (link.endsWith('/trigger/solicitorReviewAndConfirm/solicitorReviewAndConfirmsolicitorReviewLegalStatementPage1'));

    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 3);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 1);
    

    await I.click(linkLocator);
    await I.waitForNavigationToComplete();
}
