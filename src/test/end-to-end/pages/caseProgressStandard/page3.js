const assert = require('assert');

module.exports = async function () {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('a[aria-controls="caseProgressTab"][aria-selected=true]'); 

    // Check text on lhs side is all correct.
    const texts = await I.grabTextFrom('markdown  p.govuk-body-s');
    assert (texts.length === 17);
    assert (texts[0] === 'These steps are to be completed by the legal professional.');
    assert (texts[1] === 'Add solicitor details');
    assert (texts[2] === 'Add deceased details');
    assert (texts[3] === 'Add application details');
    assert (texts[4] === 'These steps are to be completed by the legal professional.');
    assert (texts[5] === 'Review and sign legal statement and submit application');
    assert (texts[6] === 'The legal statement is generated. You can review, change any details, then sign and submit your application.');
    assert (texts[7] === 'Send documents\n');
    assert (texts[8] === 'These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.');
    assert (texts[9] === 'Authenticate documents');
    assert (texts[10] === 'We will authenticate your documents and match them with your application.');
    assert (texts[11] === 'Examine application');
    assert (texts[12] === 'We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.');
    assert (texts[13] === 'Your application will update through any of these case states as it is reviewed by our team:');
    assert (texts[14] === 'This step is completed by HM Courts and Tribunals Service staff.');
    assert (texts[15] === 'Issue grant of representation');
    assert (texts[16] === 'The grant will be delivered in the post a few days after issuing.');

    await I.seeNumberOfVisibleElements('p.govuk-body-s a', 1);
    const linkText = await I.grabTextFrom('p.govuk-body-s a');
    assert (linkText === 'Add deceased details'); //assert deceased details has a link

    const link = await I.grabAttributeFrom('p.govuk-body-s a', 'href');
    assert (link.endsWith('/trigger/solicitorUpdateApplication/solicitorUpdateApplicationsolicitorUpdateApplicationPage1'));

    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 1);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 1);

    const imgSrcCompleted = await I.grabAttributeFrom('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 'src');
    assert (imgSrcCompleted.endsWith('completed.png'));

    const imgSrcNotStarted = await I.grabAttributeFrom('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 'src');
    assert (imgSrcNotStarted.endsWith('not-started.png'));

    await I.click('p.govuk-body-s a');
    await I.waitForNavigationToComplete();
}
