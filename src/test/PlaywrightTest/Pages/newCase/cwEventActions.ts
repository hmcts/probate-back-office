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

  constructor(public readonly page: Page) {
    super(page);
  }

  async chooseNextStep(nextStep: string) {
    await this.verifyPageLoad(this.nextStepLocator);
    await expect(this.nextStepLocator).toBeEnabled();
    await this.nextStepLocator.selectOption({ label: nextStep });
    // await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
    await this.waitForNavigationToComplete(this.goButtonLocator);
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
    await this.verifyPageLoad(this.page.getByRole("heading", { name: documentUploadConfig.waitForText }));
    await expect(
      this.page.getByRole("heading", {
        name: documentUploadConfig.waitForText,
        exact: true,
      })
    ).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.addNewLocator).toBeEnabled();
    await this.addNewLocator.focus();
    await this.addNewLocator.click();
    /*if (!testConfig.TestAutoDelayEnabled) {
      await this.page.waitForTimeout(testConfig.ManualDelayShort);
    }*/
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
    await this.waitForNavigationToComplete(commonConfig.submitButton, 10);

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
    await this.page.locator('#boCaseStopReasonList_0_caseStopSubReasonDocRequired').selectOption('7: PA11');
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }
}
