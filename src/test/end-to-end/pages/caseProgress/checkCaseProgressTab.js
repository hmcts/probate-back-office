const assert = require('assert');
const moment = require('moment');

// opts are numCompleted, numInProgress, numNotStarted, linkText, opts.linkUrl
module.exports = async function (opts) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForElement('a[aria-controls="caseProgressTab"][aria-selected=true]');

    // Check text on lhs side is all correct.
    const texts = await I.grabTextFrom('markdown  p.govuk-body-s');
    console.log('texts=>', texts);
    console.log('length => ', texts.length);
    assert (texts.length === 17);
    assert (texts[0] === 'These steps are to be completed by the probate practitioner.');
    assert (texts[1] === 'Add solicitor details');
    assert (texts[2] === 'Add deceased details');
    assert (texts[3] === 'Add application details');
    assert (texts[4] === 'These steps are to be completed by the probate practitioner.');
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

    if (opts.linkText && opts.linkUrl) {
        await I.seeNumberOfVisibleElements('p.govuk-body-s a', 1);
        const lnkTxt = await I.grabTextFrom('p.govuk-body-s a');
        assert (lnkTxt=== opts.linkText); //assert deceased details has a link

        const lnk = await I.grabAttributeFrom('p.govuk-body-s a', 'href');
        assert (lnk.endsWith(opts.linkUrl));
    } else {
        await I.seeNumberOfVisibleElements('p.govuk-body-s a', 0);
        const docsText = await I.grabTextFrom('span.govuk-details__summary-text');
        assert (docsText === 'View the documents needed by HM Courts and Tribunal Service');
    }

    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', opts.numCompleted);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="IN PROGRESS"]', opts.numInProgress);
    await I.seeNumberOfVisibleElements('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', opts.numNotStarted);

    if (opts.numCompleted > 0) {
        let imgSrcCompleted = await I.grabAttributeFrom('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]', 'src');
        if (typeof imgSrcCompleted !== 'string') {
            imgSrcCompleted = imgSrcCompleted[0];
        }
        assert (imgSrcCompleted.endsWith('completed.png'));
    }

    if (opts.numNotStarted > 0) {
        let imgSrcNotStarted = await I.grabAttributeFrom('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]', 'src');
        if (typeof imgSrcNotStarted !== 'string') {
            imgSrcNotStarted = imgSrcNotStarted[0];
        }
        assert (imgSrcNotStarted.endsWith('not-started.png'));
    }

    if (opts.numInProgress > 0) {
        let imgSrcInProgress = await I.grabAttributeFrom('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="IN PROGRESS"]', 'src');
        if (typeof imgSrcInProgress !== 'string') {
            imgSrcInProgress = imgSrcInProgress[0];
        }
        assert (imgSrcInProgress.endsWith('in-progress.png'));
    }

    const caseRef = await I.grabTextFrom('h1.heading-h1');

    if (opts.checkSubmittedDate) {
        await I.see(`Submitted on ${moment().format('DD MMM yyyy')}`);
    }
    if (opts.goToNextStep) {
        await I.waitForNavigationToComplete('button[type="submit"]');
    }
    if (opts.signOut) {
        await I.waitForNavigationToComplete('#sign-out');
    }

    return caseRef.replace('#', '');
};
