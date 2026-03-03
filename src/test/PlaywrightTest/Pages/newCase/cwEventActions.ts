import { expect, Page } from "@playwright/test";
import { testConfig } from "../../Configs/config.ts";
import newConfig from "../caseDetails/grantOfProbate/superUserCwConfig.json" with { type: "json" };
import commonConfig from "../common/commonConfig.json" with { type: "json" };
import createCaseConfig from "../createCase/createCaseConfig.json" with { type: "json" };
import documentUploadConfig from "../documentUpload/caveat/documentUploadConfig.json" with { type: "json" };
import legalDocumentUploadConfig from "../documentUpload/grantOfProbate/documentUploadConfig.json" with { type: "json" };
import emailCaveatorConfig from "../emailNotifications/caveat/emailCaveatorConfig.json" with { type: "json" };
import eventSummaryConfig from "../eventSummary/eventSummaryConfig.json" with { type: "json" };
import handleEvidenceConfig from "../handleEvidence/handleEvidenceConfig.json" with { type: "json" };
import issueGrantConfig from "../issueGrant/issueGrantConfig.json" with { type: "json" };
import registrarsDecisionConfig from "../registrarsDecision/registrarsDecisionConfig.json" with { type: "json" };
import reopenCaveatConfig from "../reopenningCases/caveat/reopenCaveatConfig.json" with { type: "json" };
import { BasePage } from "../utility/basePage.ts";
import withdrawCaveatConfig from "../withdrawCaveat/withdrawCaveatConfig.json" with { type: "json" };
import withdrawalConfig from "../withdrawal/willLodgement/withdrawalConfig.json" with { type: "json" };
import refundConfig from "../solicitorApplyProbate/makePayment/refundConfig.json" with { type: "json" };

type DocumentUploadConfig = typeof documentUploadConfig;
type LegalDocumentUploadConfig = typeof legalDocumentUploadConfig;
type WithdrawalConfig = typeof withdrawalConfig;

export class CwEventActionsPage extends BasePage {
  readonly nextStepLocator = this.page.locator("#next-step");
  readonly btnLocator = this.page.locator(
    'button.button-secondary[aria-label^="Remove Possible case matches"]'
  );
  readonly caseMatchLocator = this.page.locator("#caseMatches_0_0").first();
  readonly caseMatchValidLocator = this.page.locator("#caseMatches_0_valid_Yes");
  readonly caseMatchImportLocator = this.page.locator(
    "#caseMatches_0_doImport_No"
  );
  readonly summaryLocator = this.page.locator("#field-trigger-summary");
  readonly descriptionLocator = this.page.locator("#field-trigger-description");
  readonly continueButtonLocator = this.page.getByRole("button", {
    name: "Continue",
  });
  readonly addNewLocator = this.page.getByRole("button", { name: "Add new" });
  readonly emailCaveatorHeadingLocator = this.page.getByRole("heading", {
    name: emailCaveatorConfig.waitForText,
  });
  readonly emailLocator = this.page.locator("#messageContent");
  readonly reopenCaveatHeadingLocator = this.page.getByRole("heading", {
    name: reopenCaveatConfig.waitForText,
  });
  readonly caveatReopenReasonLocator = this.page.locator("#caveatReopenReason");
  readonly withdrawCaveatHeadingLocator = this.page.getByText(
    withdrawCaveatConfig.page1_waitForText
  );
  readonly emailRequestedLocator = this.page.locator(
    `#caveatRaisedEmailNotificationRequested_${withdrawCaveatConfig.page1_optionNo}`
  );
  readonly bulkPrintTextLocator = this.page.getByText(
    withdrawCaveatConfig.page1_send_bulk_print
  );
  readonly bulkPrintOptionLocator = this.page.locator(
    `#sendToBulkPrintRequested_${withdrawCaveatConfig.page1_optionNo}`
  );
  readonly registrarDecisionHeadingLocator = this.page.getByText(
    registrarsDecisionConfig.waitForText
  );
  // this.registrarDecisionSelectionLocator = this.page.getByLabel(`${registrarsDecisionConfig.radioProbateRefused}`);
  readonly registrarDecisionSelectionLocator = this.page
    .getByRole("radio")
    .nth(0);
  readonly registrarDecisionReasonLocator = this.page.locator(
    "#registrarDirectionToAdd_furtherInformation"
  );
  readonly handleEvidenceHeadingLocator = this.page.getByRole("heading", {
    name: `${handleEvidenceConfig.waitForText}`,
  });
  readonly handleEvideChkBoxLocator = this.page.locator(
    `#evidenceHandled_${handleEvidenceConfig.checkbox}`
  );
  readonly addCaseStopReasonLocator = this.page.locator("div.panel button");
  readonly caseStopReasonLocator = this.page.locator(
    "#boCaseStopReasonList_0_caseStopReason"
  );
  readonly resolveStopLocator = this.page.locator("#resolveStopState");
  readonly newStateLocator = this.page.locator("#transferToState");
  readonly newDobLocator = this.page.locator("#deceasedDob");
  readonly issueGrantHeadingLocator = this.page.getByRole("heading", {
    name: issueGrantConfig.waitForText,
  });
  readonly bulkPrintLocator = this.page.locator(
    `#boSendToBulkPrint_${issueGrantConfig.list1_text}`
  );
  readonly emailGrantIssueNotificationLocator = this.page.locator(
    `#boEmailGrantIssuedNotification_${issueGrantConfig.list2_text}`
  );
  readonly probateManPrint_waitForText = this.page.getByRole("heading", {
    name: "Grant Application",
  });
  readonly willWithdrawReasonLocator = this.page.locator("#withdrawalReason");
  readonly goButtonLocator = this.page.getByRole("button", { name: "Go" });
  readonly addRemissionLocator = this.page.getByRole("button", {
    name: refundConfig.addRemissionButton,
    }
  ).first();
  readonly issueRefundButtonLocator = this.page.getByRole("button", { name: refundConfig.issueRefundButton });
  readonly remissionPageHeadingLocator = this.page.getByText(refundConfig.remissionRefHeading);
  readonly remissionPageSubHeadingLocator = this.page.getByText(refundConfig.remissionRefSubHeading);
  readonly remissionAmountHeadingLocator = this.page.getByText(refundConfig.remissionAmountHeading);
  readonly cyaPageHeadingLocator = this.page.getByText(refundConfig.cyaPageHeading);
  readonly applicationFeeSelector = this.page.locator('input[name="organisation"]').first();
  readonly copiesFeeSelector = this.page.locator('input[name="organisation"]').nth(1);
  readonly returnToCaseLocator = this.page.getByRole('link', { name: refundConfig.returnToCaseLink });

  constructor(public readonly page: Page) {
    super(page);
  }

  async chooseNextStep(nextStep: string) {
    await this.verifyPageLoad(this.nextStepLocator, 10_000);
    await expect(this.nextStepLocator).toBeEnabled();
    await this.nextStepLocator.selectOption({ label: nextStep });
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(this.goButtonLocator, 10_000);
  }

  async selectCaseMatches(
    caseRef: string,
    nextStepName: string,
    retainFirstItem: boolean = true,
    addNewButtonLocator: string | null = null,
    skipMatchingInfo: boolean = false
  ) {
    await this.verifyPageLoad(this.page.getByText(nextStepName));
    await expect(this.page.getByText(nextStepName)).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    // await this.page.waitForTimeout(testConfig.CaseMatchesInitialDelay);
    const numOfElements = await this.btnLocator.count();
    if (numOfElements > 0) {
      await expect(this.caseMatchLocator).toBeVisible();
      await expect(this.caseMatchValidLocator).toBeVisible();
      // await I.waitForElement('#caseMatches_0_0', testConfig.WaitForTextTimeout);
      // await I.waitForVisible({css: '#caseMatches_0_valid_Yes'}, testConfig.WaitForTextTimeout);
    }

    if (numOfElements === 0 && retainFirstItem && addNewButtonLocator) {
      const addNewButton = this.page.getByText(addNewButtonLocator as string);
      /*await this.page.waitForTimeout(
        testConfig.CaseMatchesAddNewButtonClickDelay
      );*/
      await expect(addNewButton).toBeEnabled();
      await addNewButton.click();
    }

    if (retainFirstItem && (numOfElements > 0 || addNewButtonLocator)) {
      // Just a small delay - occasionally we get issues here but only relevant for local dev.
      // Only necessary where we have no auto delay (local dev).
      /*if (!testConfig.TestAutoDelayEnabled) {
        await this.page.waitForTimeout(testConfig.ManualDelayMedium);
      }*/
      await expect(this.caseMatchValidLocator).toBeEnabled();
      await this.caseMatchValidLocator.focus();
      await this.caseMatchValidLocator.check();
      await expect(this.caseMatchImportLocator).toBeEnabled();
      await this.caseMatchImportLocator.click();
    }

    /*await this.page.evaluate(async () => {
      const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
      for (let i = 0; i < document.body.scrollHeight; i += 1000) {
        window.scrollTo(0, i);
        await delay(100);
      }
    });*/

    await this.page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));

    if (nextStepName === "Match application") {
      await this.waitForNavigationToComplete(commonConfig.continueButton);
    } else {
      await expect(this.submitButtonLocator).toBeVisible();
      await expect(this.submitButtonLocator).toBeEnabled();
      await this.submitButtonLocator.click();
    }

    if (skipMatchingInfo) {
      await expect(this.summaryLocator).toBeVisible();
      /*if (!testConfig.TestAutoDelayEnabled) {
        await this.page.waitForTimeout(testConfig.ManualDelayShort);
      }*/
      await this.waitForNavigationToComplete(commonConfig.continueButton);
    }
    // await this.page.waitForTimeout(testConfig.CaseMatchesCompletionDelay);
  }

  async selectProbateManCaseMatchesForGrantOfProbate(caseRef: string, nextStepName: string) {
    await this.verifyPageLoad(this.page.getByText(nextStepName));
    await expect(this.page.getByText(nextStepName)).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    // await this.page.waitForTimeout(testConfig.CaseMatchesInitialDelay);

    const numOfElements = await this.btnLocator.count();

    // await I.wait(testConfig.CaseMatchesInitialDelay);

    // const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

    if (numOfElements > 0) {
      await expect(this.caseMatchValidLocator).toBeVisible();
    }
    // const legacyApplication = this.page.locator('#caseMatches_%s_%s > fieldset > ccd-field-read:nth-child(2) > div > ccd-field-read-label > div > dl > dd');
    const legacyApplicationTypeText = "Legacy LEGACY APPLICATION";
    for (let i = numOfElements; i >= 0; i--) {
      const currentCaseLocator = (i - 1).toString();
      const legacyApplication = `#caseMatches_${currentCaseLocator}_${currentCaseLocator} > fieldset > ccd-field-read:nth-child(2) > div > ccd-field-read-label > div > dl > dd`;

      /*await this.page.waitForTimeout(
        testConfig.CaseMatchesLocateRemoveButtonDelay
      );*/
      const text = await this.page
        .locator(legacyApplication)
        .filter({ hasText: legacyApplicationTypeText })
        .textContent();

      if (text === legacyApplicationTypeText) {
        /*if (!testConfig.TestAutoDelayEnabled) {
          await this.page.waitForTimeout(testConfig.ManualDelayShort);
        }*/
        const caseMatchesValidYesLocatorNew = `#caseMatches_${currentCaseLocator}_valid_Yes`;
        const caseMatchesImportLocatorNew = this.page.locator(
          `#caseMatches_${currentCaseLocator}_doImport_No`
        );
        await expect(
          this.page.locator(caseMatchesValidYesLocatorNew)
        ).toBeVisible();
        await this.page
          .locator(caseMatchesValidYesLocatorNew)
          .scrollIntoViewIfNeeded();
        await expect(
          this.page.locator(caseMatchesValidYesLocatorNew)
        ).toBeEnabled();
        await this.page.locator(caseMatchesValidYesLocatorNew).click();
        await expect(caseMatchesImportLocatorNew).toBeVisible();
        await caseMatchesImportLocatorNew.click();
        break;
      }
    }
    await expect(this.submitButtonLocator).toBeVisible();
    await expect(this.submitButtonLocator).toBeEnabled();
    await this.submitButtonLocator.click();
    // await this.page.waitForTimeout(testConfig.CaseMatchesCompletionDelay);
  }

  async verifyProbateManCcdCaseNumber() {
    const probateManCaseUrlXpath =
      "//span[contains(text(),'print/probateManTypes/GRANT_APPLICATION/cases')]";

    let caseUrl = await this.page
      .locator(probateManCaseUrlXpath)
      .first()
      .textContent();

    if (caseUrl.includes("ccd-api-gateway-web")) {
      caseUrl = caseUrl.replace(
        /http(s?):\/\/.*?\/print/,
        testConfig.TestBackOfficeUrl + "/print"
      );
    }

    await this.page.goto(caseUrl);
    // await this.page.waitForTimeout(testConfig.ManualDelayMedium);
    await expect(this.probateManPrint_waitForText).toBeVisible();
    // this.amOnLoadedPage(caseUrl);
    // await I.wait(testConfig.ManualDelayMedium);
    // await I.waitForText('Grant Application', 600);
    const ccdCaseNoTextXpath = "xpath=/html/body/pre/table/tbody/tr[3]/td[1]"; // //td[text()='Ccd Case No:']
    const ccdCaseNoText = await this.page
      .locator(ccdCaseNoTextXpath)
      .textContent();
    const ccdCaseNoValueXpath = "xpath=/html/body/pre/table/tbody/tr[3]/td[2]";
    if (ccdCaseNoText === "Ccd Case No:") {
      await expect(this.page.locator(ccdCaseNoValueXpath)).toBeVisible();
    } else {
      throw new Error(
        `Ccd Case No: text xpath changed on probate man case url ${caseUrl} Page, please verify and update both text and value locators`
      );
    }
  }

  async enterEventSummary(caseRef: string, nextStepName: string) {
    // await this.page.waitForTimeout(testConfig.EventSummaryDelay);
    let eventSummaryPrefix = nextStepName;
    await this.verifyPageLoad(this.page.getByText(nextStepName));
    await expect(this.page.getByText(nextStepName)).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    eventSummaryPrefix =
      eventSummaryPrefix.replace(/\s+/g, "_").toLowerCase() + "_";
    await expect(this.summaryLocator).toBeEnabled();
    await this.summaryLocator.fill(
      eventSummaryPrefix + eventSummaryConfig.summary
    );
    await this.descriptionLocator.fill(
      eventSummaryPrefix + eventSummaryConfig.comment
    );
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async uploadDocument(caseRef: string, documentUploadConfig: DocumentUploadConfig) {
    await this.verifyPageLoad(this.page.getByRole("heading", { name: documentUploadConfig.waitForText }), 10_000);
    await expect(
      this.page.getByRole("heading", {
        name: documentUploadConfig.waitForText,
        exact: true,
      })
    ).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.addNewLocator).toBeEnabled();
    await this.addNewLocator.focus();
    await this.addNewLocator.click({ timeout: testConfig.ManualDelayShort });
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_Comment`)
    ).toBeVisible();
    // await this.page.waitForTimeout(2);
    await this.page
      .locator(`${documentUploadConfig.id}_0_Comment`)
      .fill(documentUploadConfig.comment);
    // await this.page.waitForTimeout(1);
    /*if (!testConfig.TestAutoDelayEnabled) {
      await this.page.waitForTimeout(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }*/

    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_Comment`)
    ).toHaveValue(documentUploadConfig.comment);
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_DocumentType`)
    ).toBeVisible();
    await this.page
      .locator(`${documentUploadConfig.id}_0_DocumentType`)
      .selectOption(documentUploadConfig.documentType[0]);
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)
    ).toBeVisible();
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)
    ).toBeEnabled();
    // await this.page.waitForTimeout(3);
    await this.page
      .locator(`${documentUploadConfig.id}_0_DocumentLink`)
      .setInputFiles(`${documentUploadConfig.fileToUploadUrl}`);
    await this.waitForUploadToBeCompleted();
    // await this.page.waitForTimeout(testConfig.DocumentUploadDelay);

    if (documentUploadConfig.documentType) {
      for (let i = 0; i < documentUploadConfig.documentType.length; i++) {
        const optText = this.page
          .locator(
            `${documentUploadConfig.id}_0_DocumentType option:nth-child(${
              i + 2
            })`
          )
        if ((await optText.textContent()) !== documentUploadConfig.documentType[i]) {
          console.info("document upload doc types not as expected.");
          console.info(
            `expected: ${documentUploadConfig.documentType[i]}, actual: ${optText}`
          );
          console.info("doctype select html:");
          console.info(
            await this.page
              .locator(`${documentUploadConfig.id}_0_DocumentType`)
              .all()
          );
        }
        console.info(
          "Document upload type number " +
            (i + 1) +
            " in list - " +
            documentUploadConfig.documentType[i]
        );
        await expect(optText).toHaveText(documentUploadConfig.documentType[i])
      }
    }
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)
    ).toBeVisible();
    // await this.page.waitForTimeout(3);
    await expect(
      this.page.locator(`${documentUploadConfig.id}_0_Comment`)
    ).toHaveValue(documentUploadConfig.comment);
    // small delay to allow hidden vars to be set
    // await this.page.waitForTimeout(testConfig.DocumentUploadDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async uploadLegalStatement(caseRef: string, documentUploadConfig: LegalDocumentUploadConfig) {
    await expect(
      this.page.getByRole("heading", {
        name: documentUploadConfig.legalStatement_waitForText,
        exact: true,
      })
    ).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(
      this.page.locator(`${documentUploadConfig.uploadLegalStatementId}`)
    ).toBeVisible();
    await expect(
      this.page.locator(`${documentUploadConfig.uploadLegalStatementId}`)
    ).toBeEnabled();
    // await this.page.waitForTimeout(3);
    await this.page
      .locator(`${documentUploadConfig.uploadLegalStatementId}`)
      .setInputFiles(`${documentUploadConfig.fileToUploadUrl}`);
    await this.waitForUploadToBeCompleted();
    //await this.page.waitForTimeout(testConfig.DocumentUploadDelay);
  }

  async emailCaveator(caseRef: string) {
    await this.verifyPageLoad(this.emailCaveatorHeadingLocator);
    await expect(this.emailCaveatorHeadingLocator).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.emailLocator).toBeEnabled();
    await this.emailLocator.fill(emailCaveatorConfig.email_message_content);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async reopenCaveat(caseRef: string) {
    await this.verifyPageLoad(this.reopenCaveatHeadingLocator);
    await expect(this.reopenCaveatHeadingLocator).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.caveatReopenReasonLocator).toBeEnabled();
    await this.caveatReopenReasonLocator.fill(
      reopenCaveatConfig.reopen_caveat_reason
    );
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async withdrawCaveatPage1() {
    await this.verifyPageLoad(this.withdrawCaveatHeadingLocator);
    await expect(this.withdrawCaveatHeadingLocator).toBeVisible();
    await expect(this.emailRequestedLocator).toBeEnabled();
    await this.emailRequestedLocator.focus();
    await this.emailRequestedLocator.check();
    await expect(this.bulkPrintTextLocator).toBeVisible();
    await this.bulkPrintOptionLocator.focus();
    await this.bulkPrintOptionLocator.check();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async registrarsDecision(caseRef: string) {
    await this.verifyPageLoad(this.registrarDecisionHeadingLocator);
    await expect(this.registrarDecisionHeadingLocator).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.registrarDecisionSelectionLocator).toBeEnabled();
    await this.registrarDecisionSelectionLocator.focus();
    await this.registrarDecisionSelectionLocator.click();
    // await this.page.waitForTimeout(3);
    await this.registrarDecisionSelectionLocator.click();
    await this.registrarDecisionReasonLocator.fill(
      registrarsDecisionConfig.furtherInformation
    );

    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async handleEvidence(caseRef: string, handled: string = "No") {
    await this.verifyPageLoad(this.handleEvidenceHeadingLocator);
    await expect(this.handleEvidenceHeadingLocator).toBeVisible();
    const caseRefLocator = this.page.getByRole("heading", {
      name: `${caseRef}`,
    });
    const handleEvidenceChkBoxOptionLocator = this.page.locator(
      `#evidenceHandled_${handled}`
    );
    await expect(caseRefLocator).toBeVisible();
    await expect(this.handleEvideChkBoxLocator).toBeEnabled();
    await handleEvidenceChkBoxOptionLocator.isChecked();
    await this.handleEvideChkBoxLocator.click();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async caseProgressStopEscalateIssueAddCaseStoppedReason() {
    await this.verifyPageLoad(this.addCaseStopReasonLocator);
    await expect(this.addCaseStopReasonLocator).toBeEnabled();
    await this.addCaseStopReasonLocator.click();
    await expect(this.caseStopReasonLocator).toBeEnabled();
    await this.caseStopReasonLocator.click();
    await this.caseStopReasonLocator.selectOption({
      label: `${createCaseConfig.stopReason}`,
    });
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton, 10_000);

  }

  async chooseResolveStop(resolveStop: string) {
    await this.verifyPageLoad(this.resolveStopLocator);
    await expect(this.resolveStopLocator).toBeEnabled();
    await this.resolveStopLocator.selectOption({ label: `${resolveStop}` });
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async issueGrant(caseRef: string) {
    await this.verifyPageLoad(this.issueGrantHeadingLocator);
    await expect(this.issueGrantHeadingLocator).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.bulkPrintLocator).toBeEnabled();
    await this.bulkPrintLocator.click();
    await expect(this.emailGrantIssueNotificationLocator).toBeEnabled();
    await this.emailGrantIssueNotificationLocator.click();
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async enterNewDob(updatedDoB: string) {
    await this.verifyPageLoad(this.page.getByRole("heading", { name: newConfig.waitForText }));
    await expect(
      this.page.getByRole("heading", { name: newConfig.waitForText })
    ).toBeVisible();
    await expect(this.newDobLocator).toBeEnabled();
    await this.newDobLocator.fill(updatedDoB);
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async chooseNewState(newState: string) {
    await this.verifyPageLoad(this.page.getByRole("heading", { name: newConfig.newState_waitForText }));
    await expect(
      this.page.getByRole("heading", { name: newConfig.newState_waitForText })
    ).toBeVisible();
    await expect(this.newStateLocator).toBeEnabled();
    await this.newStateLocator.selectOption({ label: `${newState}` });
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async selectWithdrawalReason(caseRef: string, withdrawalConfig: WithdrawalConfig) {
    await this.verifyPageLoad(this.page.getByText(withdrawalConfig.waitForText));
    await expect(
      this.page.getByText(withdrawalConfig.waitForText)
    ).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await this.willWithdrawReasonLocator.selectOption({
      label: `${withdrawalConfig.list1_text}`,
    });
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async caseProgressSelectEscalateReason() {
    await this.verifyPageLoad(this.page.locator('#registrarEscalateReason'));
    await expect(this.page.locator('#registrarEscalateReason')).toBeEnabled();
    await this.page.locator('#registrarEscalateReason').selectOption('1: referrals');
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async caseProgressStopEscalateIssueCaseStopAgainReason() {
    await this.verifyPageLoad(this.page.locator('#boCaseStopReasonList_0_caseStopReason'));
    await expect(this.page.locator('#boCaseStopReasonList_0_caseStopReason')).toBeEnabled();
    await this.page.locator('#boCaseStopReasonList_0_caseStopReason').selectOption('10: DocumentsRequired');
    await expect(this.page.locator('#boCaseStopReasonList_0_caseStopSubReasonDocRequired')).toBeEnabled();
    await this.page.locator('#boCaseStopReasonList_0_caseStopSubReasonDocRequired').selectOption('9: PA11');
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async addRemissionAndRefund(caseRef) {
    const confirmationText = refundConfig.remissionConfirmationText2.replace('{remAmount}', refundConfig.remissionAmount);
    await expect(this.page.getByRole('heading', { name: new RegExp(caseRef) })).toBeVisible();
    await expect(this.addRemissionLocator).toBeEnabled();
    await this.addRemissionLocator.focus();
    await this.addRemissionLocator.click();

    await expect(this.remissionPageHeadingLocator).toBeVisible();
    await expect(this.remissionPageSubHeadingLocator).toBeVisible();
    await this.page.locator('#remissionCode').fill(refundConfig.remissionReference);
    await this.continueButtonLocator.click();
    await expect(this.remissionPageSubHeadingLocator).not.toBeVisible();

    await expect(this.remissionAmountHeadingLocator).toBeVisible();
    await this.page.locator('#amount').fill(refundConfig.remissionAmount);
    await this.page.getByRole('button', { name: 'Continue' }).click();
    await expect(this.remissionAmountHeadingLocator).not.toBeVisible();

    await expect(this.cyaPageHeadingLocator).toBeVisible();
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.remissionReference) })).toBeVisible();
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.remissionAmount) }).first()).toBeVisible();
    await this.addRemissionLocator.click();
    await expect(this.page.getByText(refundConfig.remissionConfirmationText1)).toBeVisible();
    await expect(this.page.getByText(confirmationText)).toBeVisible();
    await this.continueButtonLocator.click();

    const remissionRefundRef = await this.issueRefundRequest(caseRef, true);
    return remissionRefundRef as string;
  }

  async issueRefundRequest(caseRef, remissionRefund?: boolean) {
    await expect(this.page.getByRole('heading', { name: new RegExp(caseRef) }).first()).toBeVisible();
    if (!remissionRefund) {
      await this.issueRefundPage1(caseRef);
      await this.issueRefundPage2(caseRef);
      await this.issueRefundPage3();
      await this.issueRefundCyaPage(caseRef);
      const refundRef = await this.issueRefundConfirmationPage();
      return refundRef as string;
    } else {
      await this.issueRefundPage3();
      await this.issueRefundCyaPage(caseRef, true);
      const remissionRefundRef = await this.issueRefundConfirmationPage(true);
      return remissionRefundRef as string;
    }

  }

  async issueRefundPage1(caseRef) {

    await expect(this.issueRefundButtonLocator).toBeEnabled();
    await this.issueRefundButtonLocator.focus();
    await this.issueRefundButtonLocator.click();

    await expect(this.page.getByText(refundConfig.refundHeading)).toBeVisible();
    await expect(this.page.getByRole('heading', { name: new RegExp(caseRef) }).first()).toBeVisible();
    await expect(this.page.getByText(refundConfig.refundSubHeading)).toBeVisible();
    await expect(this.applicationFeeSelector).toBeEnabled();
    await this.applicationFeeSelector.click();
    await expect(this.applicationFeeSelector).toBeChecked();
    await expect(this.copiesFeeSelector).toBeEnabled();
    await this.copiesFeeSelector.click();
    await expect(this.copiesFeeSelector).toBeChecked();
    await this.page.locator(`input[formcontrolname="refund_amount"]`).first().fill(refundConfig.refundAmountForApplication);
    await this.page.locator(`input[formcontrolname="updated_volume"]`).clear();
    await this.page.locator(`input[formcontrolname="updated_volume"]`).fill(refundConfig.refundNumberOfCopies);
    await this.page.locator(`input[formcontrolname="refund_amount"]`).nth(1).fill(refundConfig.refundAmountForCopies);
    await expect(this.continueButtonLocator).toBeEnabled();
    await this.continueButtonLocator.click();
    await expect(this.page.getByText(refundConfig.refundReasonHeading)).toBeVisible();
  }

  async issueRefundPage2(caseRef, changeRequired: boolean = false) {
    await expect(this.page.getByText(refundConfig.refundHeading)).toBeVisible();
    await expect(this.page.getByRole('heading', { name: new RegExp(caseRef) }).first()).toBeVisible();
    await expect(this.page.getByText(refundConfig.refundReasonHeading)).toBeVisible();
    if(changeRequired) {
      await expect(this.page.locator(refundConfig.changeRefundReasonLocator)).toBeVisible();
      await expect(this.page.locator(refundConfig.changeRefundReasonLocator)).toBeEnabled();
      await this.page.locator(refundConfig.changeRefundReasonLocator).click();
      await expect(this.page.locator(refundConfig.changeRefundReasonLocator)).toBeChecked();
    } else {
      await expect(this.page.locator(refundConfig.refundReasonLocator)).toBeVisible();
      await expect(this.page.locator(refundConfig.refundReasonLocator)).toBeEnabled();
      await this.page.locator(refundConfig.refundReasonLocator).click();
      await expect(this.page.locator(refundConfig.refundReasonLocator)).toBeChecked();
    }
    await expect(this.continueButtonLocator).toBeEnabled();
    await this.continueButtonLocator.click();
  }

  async issueRefundPage3() {
    await expect(this.page.getByText(refundConfig.refundHeading)).toBeVisible();
    await expect(this.page.getByText(refundConfig.contactInformationHeading)).toBeVisible();
    await expect(this.page.getByRole('radio', { name: 'Email' })).toBeVisible();
    await expect(this.page.getByRole('radio', { name: 'Post' })).toBeEnabled();
    await expect(this.page.getByRole('radio', { name: 'Email' })).toBeChecked();
    await expect(this.page.locator('#email')).toBeEnabled();
    await this.page.locator('#email').fill(refundConfig.contactEmail);
    await this.continueButtonLocator.click();
  }

  async issueRefundCyaPage(caseRef, remissionRefund: boolean = false, isUpdated: boolean = false) {
    await expect(this.cyaPageHeadingLocator).toBeVisible();
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.contactEmail) })).toBeVisible();
    if (remissionRefund) {
      await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.remissionAmount) }).first()).toBeVisible();
      await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.remissionReference) })).toBeVisible();
      await expect(this.page.getByRole('cell', { name: refundConfig.remissionRefundReason })).toBeVisible();
      await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.remissionAmount) }).nth(1)).toBeVisible();
    } else {
      if (isUpdated) {
        await expect(this.page.getByRole('cell', { name: refundConfig.updatedRefundReason })).toBeVisible();
      } else {
        await expect(this.page.getByRole('cell', { name: refundConfig.refundReasonText })).toBeVisible();
      }
      await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.refundAmount) })).toBeVisible();
    }
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.notificationText) })).toBeVisible();
    await expect(this.page.getByRole('link', { name: 'Preview' })).toBeVisible();
    await this.page.getByRole('button', { name: refundConfig.submitRefundButton}).click();
  }

  async issueRefundConfirmationPage(remissionRefund: boolean = false) {
    let confirmationText;

    await expect(this.page.getByText(refundConfig.refundConfirmationText)).toBeVisible();
    confirmationText = await this.page.getByText(refundConfig.refundReferenceText, { exact: false }).textContent();
    const refundReference = confirmationText?.match(new RegExp(`${refundConfig.refundReferenceText}\\s*([A-Z0-9-]+)`))?.[1]?.trim();
    const refundConfirmationText = refundConfig.refundConfirmationText.replace('{refAmount}', remissionRefund ? refundConfig.remissionAmount : refundConfig.totalRefundAmount);
    await expect(this.page.getByText(refundConfirmationText)).toBeVisible();
    await this.returnToCaseLocator.click();

    return refundReference as string;
  }

  async verifyAndInitiateProcessRefund(refundStatus, refundRef: string,
                              config: {
                                hasText: string;       // → filter({ hasText: row.hasText })
                                expectedText: string;  // → toContainText(row.expectedText)
                                useRegex: boolean;
                              }[],
                              isRemission: boolean = false, isUpdated: boolean = false ) {
    let refAmount, refundReason;
    let labelCell;
    if (isRemission) {
      refAmount = refundConfig.totalRemissionRefundAmount;
      refundReason = refundConfig.remissionRefundReason;
    } else if (isUpdated) {
      refAmount = refundConfig.totalRefundAmount;
      refundReason = refundConfig.updatedRefundReason;
    } else {
      refAmount = refundConfig.totalRefundAmount;
      refundReason = refundConfig.refundReasonText;
    }
    const dynamicData = {
      refundRef: refundRef,
      refundReason,
      refAmount
    };
    const tables = this.page.locator('table');
    const refundsDetailsTable = tables.nth(1);
    const refundsNotificationTable = tables.nth(2);
    const refundsStatusTable = tables.nth(3);

    for (const row of config) {
      const resolvedExpectedText = await this.resolvePlaceholders(
        row.expectedText,
        dynamicData
      );
      labelCell = refundsDetailsTable.locator('td:first-child', { hasText: row.hasText });
      await expect(labelCell).toHaveCount(1);

      const tableRow = labelCell.locator('xpath=ancestor::tr[1]');
      const valueCell = tableRow.locator('td').nth(1);

      if (row.useRegex) {
        await expect(valueCell).toHaveText(
          new RegExp(resolvedExpectedText, 'i')
        );

      } else {
        await expect(valueCell).toHaveText(resolvedExpectedText);
      }
    }

    //To verify the refund status
    if (refundStatus === 'Initiated') {
      if (isUpdated) {
        const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus1});
        await expect(refundStatusLocator).toHaveCount(2);
      } else {
        const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus1});
        await expect(refundStatusLocator).toHaveCount(1);
      }

      await this.page.getByRole('button', { name: refundConfig.processRefundButton }).click();
    } else if (refundStatus === 'Approved') {
      const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus2});
      await expect(refundStatusLocator).toHaveCount(1);
      await this.page.getByRole('link', { name: 'Back', exact: true }).click();
    } else if (refundStatus === refundConfig.refundStatus3) {
      const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus3});
      await expect(refundStatusLocator).toHaveCount(1);
      await this.page.getByRole('button', { name: refundConfig.changeRefundDetailsButton }).click();
    } else if (refundStatus === refundConfig.refundStatus4) {
      const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus4});
      await expect(refundStatusLocator).toHaveCount(1);
      await this.page.getByRole('link', { name: 'Back', exact: true }).click();
    } else if (refundStatus === refundConfig.refundStatus5) {
      labelCell = refundsNotificationTable.locator('td:nth-child(3)', {
        hasText: new RegExp(refundConfig.contactEmail.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'))
      });
      await expect(labelCell).toHaveCount(1);
      const refundStatusLocator = refundsStatusTable.locator('td:first-child', {hasText: refundConfig.refundStatus5});
      await expect(refundStatusLocator).toHaveCount(1);
    }
  }

  async submitRefundProcess(caseRef, refundProcess: string, isRemission: boolean = false, isUpdated: boolean = false) {
    if (isRemission) {
      await expect(this.page.getByRole('cell', { name: refundConfig.remissionRefundReason })).toBeVisible();
      await expect(this.page.getByRole('cell', { name: refundConfig.totalRemissionRefundAmount, exact: true })).toBeVisible();
    } else if (isUpdated) {
      await expect(this.page.getByRole('cell', { name: refundConfig.updatedRefundReason })).toBeVisible();
    } else {
      await expect(this.page.getByRole('cell', { name: refundConfig.refundReasonText })).toBeVisible();
      await expect(this.page.getByRole('cell', { name: refundConfig.refundAmount }).first()).toBeVisible();
    }
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.contactEmail) })).toBeVisible();
    await expect(this.page.getByRole('cell', { name: new RegExp(refundConfig.notificationText) })).toBeVisible();
    await expect(this.page.getByRole('link', { name: 'Preview' })).toBeVisible();
    if (refundProcess === refundConfig.refundProcessApprove) {
      await expect(this.page.locator('#refundAction-0')).toBeEnabled();
      await this.page.locator('#refundAction-0').click();
      await expect(this.page.getByRole('button', { name: 'Submit' })).toBeEnabled();
      await this.page.getByRole('button', { name: 'Submit' }).click();
    } else if (refundProcess === refundConfig.refundProcessReject) {
      await expect(this.page.locator('#refundAction-1')).toBeEnabled();
      await this.page.locator('#refundAction-1').click();
      await expect(this.page.locator('#refundRejectReason-1')).toBeEnabled();
      await this.page.locator('#refundRejectReason-1').click();
      await expect(this.page.getByRole('button', { name: 'Submit' })).toBeEnabled();
      await this.page.getByRole('button', { name: 'Submit' }).click();
    } else if (refundProcess === refundConfig.refundProcessReturnToCw) {
      await expect(this.page.locator('#refundAction-2')).toBeEnabled();
      await this.page.locator('#refundAction-2').click();
      await this.page.locator('#sendmeback').fill(refundConfig.refundProcessReturnToCw);
      await expect(this.page.getByRole('button', { name: 'Submit' })).toBeEnabled();
      await this.page.getByRole('button', { name: 'Submit' }).click();
    } else if (refundProcess === 'changeRefundDetails') {
      const tables = this.page.locator('table');
      const selectTable = tables.nth(1);
      const refCell = selectTable.locator('td').filter({
        hasText: `${refundConfig.refundReasonText}`
      });
      await expect(refCell).toHaveCount(1);
      const tableRow = refCell.locator('xpath=ancestor::tr[1]');
      await tableRow.locator('a', { hasText: 'Change' }).click();
      await this.issueRefundPage2(caseRef, true);
      await this.issueRefundCyaPage(caseRef, false, true);
      await this.issueRefundConfirmationPage(true);
    }

  }

  async verifyRefundConfirmation(confirmationText: string) {
    await expect(this.page.getByRole('heading', { name: confirmationText })).toBeVisible();
    await this.returnToCaseLocator.click();
  }
}
