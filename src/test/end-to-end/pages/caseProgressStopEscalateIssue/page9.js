'use strict';
const assert = require('assert');

module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('a[aria-controls="caseProgressTab"][aria-selected=true]'); 

    // Check text on lhs side is all correct.
    const texts = await I.grabTextFrom('markdown  p.govuk-body-s');
    assert (texts.length === 17);

    const linkText = await I.grabTextFrom('p.govuk-body-s a');
    assert (linkText === 'Add application details'); //assert application details has a link

    await I.seeNumberOfVisibleElements('p.govuk-body-s a', 1);

    const link = await I.grabAttributeFrom('p.govuk-body-s a', 'href');
    assert (link.endsWith('/trigger/solicitorUpdateProbate/solicitorUpdateProbatesolicitorUpdateProbatePage1'));

    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 2);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 1);

    await I.waitForNavigationToComplete('p.govuk-body-s a');
}
