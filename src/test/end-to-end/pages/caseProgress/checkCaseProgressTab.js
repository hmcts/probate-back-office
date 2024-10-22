'use strict';
const assert = require('assert');
const moment = require('moment');
const testConfig = require('src/test/config.js');

// opts are numCompleted, numInProgress, numNotStarted, linkText, opts.linkUrl
module.exports = async function (opts) {
    const I = this;
    // if this hangs, then case progress tab has not been generated / not been generated correctly and test fails
    await I.waitForText('Case Progress', testConfig.WaitForTextTimeout || 60);

    // Check text on lhs side is all correct.
    await I.wait(3);
    const texts = await I.grabTextFromAll('markdown  p.govuk-body-s');
    await I.wait(3);
    assert (texts.length === 38);
    assert (texts[1] === 'These steps are to be completed by the Probate practitioner.');
    assert (texts[3] === 'Add Probate practitioner details');
    assert (texts[5] === 'Add deceased details');
    assert (texts[7] === 'Add application details');
    assert (texts[9] === 'These steps are to be completed by the Probate practitioner.');
    assert (texts[11] === 'Review and sign legal statement and submit application');
    assert (texts[12] === 'The legal statement is generated. You can review, change any details, then sign and submit your application.');
    assert (texts[15] === 'Make payment');
    await I.wait(3);
    if (texts[17] === '') {
        assert (texts[17] === '');
    } else {
        assert (texts[17] === 'Once payment is made, you\'ll need to refresh the page or re-enter the case for the payment status to update.');
    }
    assert (texts[19] === 'Send documents\n');
    assert (texts[20] === 'These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.');
    assert (texts[22] === 'Authenticate documents');
    assert (texts[23] === 'We will authenticate your documents and match them with your application.');
    assert (texts[26] === 'Examine application');
    assert (texts[27] === 'We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.');
    assert (texts[28] === 'Your application will update through any of these case states as it is reviewed by our team:');
    assert (texts[32] === 'This step is completed by HM Courts and Tribunals Service staff.');
    assert (texts[34] === 'Issue grant of representation');
    assert (texts[35] === 'The grant will be delivered in the post a few days after issuing.');

    if (opts.linkText && opts.linkUrl) {
        await I.seeNumberOfVisibleElements('p.govuk-body-s a', 1);
        const lnkTxt = await I.grabTextFrom('p.govuk-body-s a');
        assert (lnkTxt === opts.linkText); //assert deceased details has a link

        const lnk = await I.grabAttributeFrom('p.govuk-body-s a', 'href');
        assert (lnk.endsWith(opts.linkUrl));
    } else {
        await I.seeNumberOfVisibleElements('p.govuk-body-s a', 0);
        const docsText = await I.grabTextFrom('span.govuk-details__summary-text');
        assert.equal(docsText, '\n      View the documents needed by HM Courts and Tribunal Service\n    ');
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
        await I.caseProgressSelectPenultimateNextStepAndGo();
    }
    if (opts.signOut) {
        await I.signOut();
    }

    return caseRef.replace('#', '');
};
