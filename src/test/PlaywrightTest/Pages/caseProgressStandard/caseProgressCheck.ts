import { expect, Page } from "@playwright/test";
import {SignInPage} from "../IDAM/signIn.ts";
import assert from "assert";
import moment from "moment";
import commonConfig from "../common/commonConfig.json" with { type: "json" };
import caseProgressConfig from "../caseProgressAppStopped/caseProgressConfig.json" with { type: "json" };

export class CaseProgressPage extends SignInPage {
  readonly deceasedForenameLocator = this.page.locator("#deceasedForenames");
  readonly goButtonLocator = this.page.getByRole("button", { name: "Go" });
  readonly caseProgressHeadingLocator = this.page.getByLabel('Case Progress', { exact: true });

  constructor(public readonly page: Page) {
    super(page);
  }

  async caseProgressCheckCaseProgressTab(opts) {
    await this.verifyPageLoad(this.caseProgressHeadingLocator);
    await expect(this.caseProgressHeadingLocator).toBeVisible();
    await this.page.getByRole("tab", { name: 'Case Progress' }).focus();
    await this.page.getByRole("tab", { name: 'Case Progress' }).click();
    await this.page.getByRole("tab", { name: 'Case Progress' }).click();
    const texts = await this.page.locator('markdown  p.govuk-body-s').allTextContents();

    // Check text on lhs side is all correct.
    assert (texts.length === 37);
    assert (texts[0] === 'These steps are to be completed by the Probate practitioner.');
    assert (texts[2] === 'Add Probate practitioner details');
    assert (texts[4] === 'Add deceased details');
    assert (texts[6] === 'Add application details');
    assert (texts[8] === 'These steps are to be completed by the Probate practitioner.');
    assert (texts[10] === 'Review and sign legal statement and submit application');
    assert (texts[11] === 'The legal statement is generated. You can review, change any details, then sign and submit your application.');
    assert (texts[14] === 'Make payment');
    // await I.wait(3);
    if (texts[17] === '') {
      assert (texts[17] === '');
    } else {
      assert (texts[17] === 'Once payment is made, you\'ll need to refresh the page or re-enter the case for the payment status to update.');
    }

    const splitText = await this.page.locator('markdown  p.govuk-body-s').nth(18).innerText();

    assert (splitText.split('\n')[0] === 'Send documents');
    assert (texts[19] === 'These steps are completed by HM Courts and Tribunals Service staff. It can take a few weeks before the review starts.');
    assert (texts[21] === 'Authenticate documents');
    assert (texts[22] === 'We will authenticate your documents and match them with your application.');
    assert (texts[25] === 'Examine application');
    assert (texts[26] === 'We review your application for incomplete information or problems and validate it against other cases or caveats. After the review we prepare the grant.');
    assert (texts[27] === 'Your application will update through any of these case states as it is reviewed by our team:');
    assert (texts[31] === 'This step is completed by HM Courts and Tribunals Service staff.');
    assert (texts[33] === 'Issue grant of representation');
    assert (texts[34] === 'The grant will be delivered in the post a few days after issuing.');

    if (opts.linkText && opts.linkUrl) {
      await expect(this.page.locator('p.govuk-body-s a')).toHaveCount(2);
      const lnkTxt = await this.page.locator('p.govuk-body-s a').first().innerText();
      assert (lnkTxt === opts.linkText); //assert deceased details has a link

      const lnk = await this.page.locator('p.govuk-body-s a').first().getAttribute('href');
      assert (lnk.endsWith(opts.linkUrl));
    } else {
      await expect(this.page.locator('p.govuk-body-s a')).toHaveCount(0);
      const docsText = await this.page.locator('span.govuk-details__summary-text').first().innerText();
      assert.equal(docsText, 'View the documents needed by HM Courts and Tribunal Service');
    }

    await expect(this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]')).toHaveCount(opts.numCompleted);
    await expect(this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="IN PROGRESS"]')).toHaveCount(opts.numInProgress);
    await expect(this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]')).toHaveCount(opts.numNotStarted);

    if (opts.numCompleted > 0) {
      let imgSrcCompleted = await this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt=COMPLETED]').first().getAttribute('src');
      if (typeof imgSrcCompleted !== 'string') {
        imgSrcCompleted = imgSrcCompleted[0];
      }
      assert (imgSrcCompleted.endsWith('completed.png'));
    }

    if (opts.numNotStarted > 0) {
      let imgSrcNotStarted = await this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="NOT STARTED"]').getAttribute('src');
      if (typeof imgSrcNotStarted !== 'string') {
        imgSrcNotStarted = imgSrcNotStarted[0];
      }
      assert (imgSrcNotStarted.endsWith('not-started.png'));
    }

    if (opts.numInProgress > 0) {
      let imgSrcInProgress = await this.page.locator('.govuk-grid-row .govuk-grid-row .govuk-grid-column-one-third img[alt="IN PROGRESS"]').first().getAttribute('src');
      if (typeof imgSrcInProgress !== 'string') {
        imgSrcInProgress = imgSrcInProgress[0];
      }
      assert (imgSrcInProgress.endsWith('in-progress.png'));
    }

    const caseRef = await this.page.locator('h1.heading-h1').innerText();

    if (opts.checkSubmittedDate) {
      await expect(this.page.getByText(`Submitted on ${moment().format('DD MMM yyyy')}`, { exact: true })).toBeVisible()
    }
    if (opts.goToNextStep) {
      await this.caseProgressSelectPenultimateNextStepAndGo();
    }
    if (opts.signOut) {
      await this.signOut();
    }

    return caseRef.replace('#', '');
  }

  async caseProgressSelectPenultimateNextStepAndGo() {
    await this.verifyPageLoad(this.page.locator('#next-step'));
    await expect(this.page.locator('#next-step')).toBeEnabled();
    const penultimateOpt = await this.page.locator('#next-step option:nth-last-child(2)').innerText();

    if (penultimateOpt === 'Delete') {
      const penultimateOptNew = await this.page.locator('#next-step option:nth-child(3)').getAttribute('value');
      await this.page.locator('#next-step').selectOption(penultimateOptNew);
    } else {
      const penultimateOptFinal = await this.page.locator('#next-step option:nth-last-child(2)').getAttribute('value');
      await this.page.locator('#next-step').selectOption(penultimateOptFinal);
    }

    // await this.waitForNavigationToComplete('button[type="submit"].button', 10_000);
    await this.clickGoButton();
  }

  async caseProgressSelectPenultimateNextStep() {
    await this.verifyPageLoad(this.page.locator('#next-step'));
    await expect(this.page.locator('#next-step')).toBeEnabled();
    const penultimateOpt = await this.page.locator('#next-step option:nth-last-child(2)').innerText();

    if (penultimateOpt === 'Delete') {
      const penultimateOptNew = await this.page.locator('#next-step option:nth-child(3)').getAttribute('value');
      await this.page.locator('#next-step').selectOption(penultimateOptNew);
    } else {
      const penultimateOptFinal = await this.page.locator('#next-step option:nth-last-child(2)').getAttribute('value');
      await this.page.locator('#next-step').selectOption(penultimateOptFinal);
    }
  }

  async clickGoButton() {
    const currentUrl = this.page.url();

    await this.page.locator('button[type="submit"].button').click({ noWaitAfter: true });

    // Wait for URL to be anything different
    let attempts = 0;
    while (this.page.url() === currentUrl && attempts < 50) {
      await this.page.reload();
      await this.page.waitForLoadState('load');
      await this.caseProgressSelectPenultimateNextStep();
      // await this.page.waitForTimeout(1000);
      await this.page.locator('button[type="submit"].button').click({ noWaitAfter: true });
      // await this.page.waitForTimeout(3000);
      attempts++;
    }

    // Additional settle time
    // await this.page.waitForTimeout(2000);
  }

  async caseProgressResumeDeceasedDetails() {
    await this.verifyPageLoad(this.deceasedForenameLocator);
    await expect(this.deceasedForenameLocator).toBeEnabled();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async caseProgressStopEscalateIssueStoppedTabCheck() {
    await this.verifyPageLoad(this.page.getByText('Case stopped', { exact: true }));
    await expect(this.page.getByText('Case stopped', { exact: true })).toBeVisible();

    // Check date format
    await expect(this.page.getByText(`The case was stopped on ${moment().format('DD MMM yyyy')} for one of two reasons:`, { exact: true })).toBeVisible();

  }

  async caseProgressStopEscalateIssueEscalatedTabCheck() {
    await this.verifyPageLoad(this.page.getByText('Case escalated to a Registrar', { exact: true }));
    await expect(this.page.getByText('Case escalated to a Registrar', { exact: true })).toBeVisible();

    // Check date format
    await expect(this.page.getByText(`The case was escalated on ${moment().format('DD MMM yyyy')}.`, { exact: true })).toBeVisible();
    // await I.waitForText(`The case was escalated on ${moment().format('DD MMM yyyy')}.`);
  }

  async caseProgressAppStoppedDetails() {
    await this.verifyPageLoad(this.page.getByText(caseProgressConfig.AppStoppedHeader, { exact: true }));
    await expect(this.page.getByText(caseProgressConfig.AppStoppedHeader, { exact: true })).toBeVisible();
    await expect(this.page.getByText(caseProgressConfig.AppStoppedReasonText, { exact: true })).toBeVisible();
    await expect(this.page.getByText(caseProgressConfig.AppStoppedAdditionalText, { exact: true })).toBeVisible();
    await this.waitForNavigationToComplete('button[type="submit"]');

    // await I.waitForText(caseProgressConfig.AppStoppedHeader);
    // await I.waitForText(caseProgressConfig.AppStoppedReasonText);
    // await I.waitForText(caseProgressConfig.AppStoppedAdditionalText);
    // await I.waitForNavigationToComplete('button[type="submit"]');
  }

  async caseProgressAppStoppedTabCheck() {
    await this.verifyPageLoad(this.page.getByText(caseProgressConfig.AppStoppedTabTitle));
    await expect(this.page.locator( 'div.govuk-inset-text').first()).toContainText(caseProgressConfig.AppStoppedTabTitle, { timeout: 2000 });
    await expect(this.page.getByText( caseProgressConfig.AppStoppedTabCheckText, { exact: true })).toBeVisible();
    // await I.waitForText(caseProgressConfig.AppStoppedTabTitle, 2, {css: 'div.govuk-inset-text'});

    // await I.waitForText(caseProgressConfig.AppStoppedTabCheckText);
  }
};
