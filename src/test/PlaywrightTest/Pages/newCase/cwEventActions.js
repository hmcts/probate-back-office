const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const {BasePage} = require ('../utility/basePage');
const commonConfig = require('../common/commonConfig');
const eventSummaryConfig = require('../eventSummary/eventSummaryConfig');
const emailCaveatorConfig = require('../emailNotifications/caveat/emailCaveatorConfig');
const reopenCaveatConfig = require('../reopenningCases/caveat/reopenCaveatConfig');
const withdrawCaveatConfig = require('../withdrawCaveat/withdrawCaveatConfig');
const registrarsDecisionConfig = require('../registrarsDecision/registrarsDecisionConfig');
const assert = require('assert');
const handleEvidenceConfig = require('../handleEvidence/handleEvidenceConfig');
// const createGrantOfProbateConfig = require('../../../end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig.json');
const createCaseConfig = require('../createCase/createCaseConfig.json');
const issueGrantConfig = require('../issueGrant/issueGrantConfig');
const newConfig = require('../caseDetails/grantOfProbate/superUserCwConfig');
// const createGrantOfProbateConfig = require('../createGrantOfProbateManual/createGrantOfProbateManualConfig.json');

exports.CwEventActionsPage = class CwEventActionsPage extends BasePage {
    constructor(page) {
        super(page);
        this.page = page;
        this.nextStepLocator = this.page.locator('#next-step');
        this.btnLocator = this.page.locator('button.button-secondary[aria-label^="Remove Possible case matches"]');
        this.caseMatchLocator = this.page.locator('#caseMatches_0_0');
        this.caseMatchValidLocator = this.page.locator('#caseMatches_0_valid_Yes');
        this.caseMatchImportLocator = this.page.locator('#caseMatches_0_doImport_No');
        this.summaryLocator = this.page.locator('#field-trigger-summary');
        this.descriptionLocator = this.page.locator('#field-trigger-description');
        this.continueButtonLocator = this.page.getByRole('button', {name: 'Continue'});
        this.addNewLocator = this.page.getByRole('button', {name: 'Add new'});
        this.emailCaveatorHeadingLocator = this.page.getByRole('heading', {name: emailCaveatorConfig.waitForText});
        this.emailLocator = this.page.locator('#messageContent');
        this.reopenCaveatHeadingLocator = this.page.getByRole('heading', {name: reopenCaveatConfig.waitForText});
        this.caveatReopenReasonLocator = this.page.locator('#caveatReopenReason');
        this.withdrawCaveatHeadingLocator = this.page.getByText(withdrawCaveatConfig.page1_waitForText);
        this.emailRequestedLocator = this.page.locator(`#caveatRaisedEmailNotificationRequested_${withdrawCaveatConfig.page1_optionNo}`);
        this.bulkPrintTextLocator = this.page.getByText(withdrawCaveatConfig.page1_send_bulk_print);
        this.bulkPrintOptionLocator = this.page.locator(`#sendToBulkPrintRequested_${withdrawCaveatConfig.page1_optionNo}`);
        this.registrarDecisionHeadingLocator = this.page.getByText(registrarsDecisionConfig.waitForText);
        // this.registrarDecisionSelectionLocator = this.page.getByLabel(`${registrarsDecisionConfig.radioProbateRefused}`);
        this.registrarDecisionSelectionLocator = this.page.getByRole('radio').nth(0);
        this.registrarDecisionReasonLocator = this.page.locator('#registrarDirectionToAdd_furtherInformation');
        this.handleEvidenceHeadingLocator = this.page.getByRole('heading', {name: `${handleEvidenceConfig.waitForText}`});
        this.handleEvideChkBoxLocator = this.page.locator(`#evidenceHandled_${handleEvidenceConfig.checkbox}`);
        this.addCaseStopReasonLocator = this.page.locator('div.panel button');
        this.caseStopReasonLocator = this.page.locator('#boCaseStopReasonList_0_caseStopReason');
        this.resolveStopLocator = this.page.locator('#resolveStopState');
        this.newStateLocator = this.page.locator('#transferToState');
        this.newDobLocator = this.page.locator('#deceasedDob');
        this.issueGrantHeadingLocator = this.page.getByRole('heading', {name: issueGrantConfig.waitForText});
        this.bulkPrintLocator = this.page.locator(`#boSendToBulkPrint_${issueGrantConfig.list1_text}`);
        this.emailGrantIssueNotificationLocator = this.page.locator(`#boEmailGrantIssuedNotification_${issueGrantConfig.list2_text}`);
        this.probateManPrint_waitForText = this.page.getByRole('heading', {name: 'Grant Application'});
        this.willWithdrawReasonLocator = this.page.locator('#withdrawalReason');
    }

    async chooseNextStep(nextStep) {
        await expect(this.nextStepLocator).toBeEnabled();
        await this.nextStepLocator.selectOption({label: nextStep});
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForGoNavigationToComplete(commonConfig.submitButton);
    }

    async selectCaseMatches(caseRef, nextStepName, retainFirstItem=true, addNewButtonLocator=null, skipMatchingInfo=false) {
        await expect.soft(this.page.getByText(nextStepName)).toBeVisible();
        await expect.soft(this.page.getByText(caseRef)).toBeVisible();
        await this.page.waitForTimeout(testConfig.CaseMatchesInitialDelay);
        const numOfElements = this.btnLocator;
        if (numOfElements > 0) {
            await expect(this.caseMatchLocator).toBeVisible();
            await expect(this.caseMatchValidLocator).toBeVisible();
        }

        if (numOfElements === 0 && retainFirstItem && addNewButtonLocator) {
            this.addNewButtonLocator = await this.page.getByText(addNewButtonLocator);
            await expect(this.addNewButtonLocator).toBeEnabled();
            await this.addNewButtonLocator.click();
        }

        if (retainFirstItem && (numOfElements > 0 || addNewButtonLocator)) {
            // Just a small delay - occasionally we get issues here but only relevant for local dev.
            // Only necessary where we have no auto delay (local dev).
            this.addNewButtonLocator = await this.page.getByText(addNewButtonLocator);
            if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }
            await this.caseMatchValidLocator.check();
            await this.caseMatchImportLocator.click();
        }

        if (nextStepName === 'Match application') {
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        } else {
            await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
        }

        if (skipMatchingInfo) {
            await expect(this.summaryLocator).toBeVisible();
            if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }
    }

    async selectProbateManCaseMatchesForGrantOfProbate(caseRef, nextStepName) {
        await expect(this.page.getByText(nextStepName)).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await this.page.waitForTimeout(testConfig.CaseMatchesInitialDelay);

        const numOfElements = await this.btnLocator.count();

        // await I.wait(testConfig.CaseMatchesInitialDelay);

        // const numOfElements = await I.grabNumberOfVisibleElements(btnLocator);

        if (numOfElements > 0) {
            await expect((this.caseMatchLocator).nth(1)).toBeVisible();
            await expect(this.caseMatchValidLocator).toBeVisible();
        }
        // const legacyApplication = this.page.locator('#caseMatches_%s_%s > fieldset > ccd-field-read:nth-child(2) > div > ccd-field-read-label > div > dl > dd');
        const legacyApplicationTypeText = 'Legacy LEGACY APPLICATION';
        /* eslint-disable no-await-in-loop */
        for (let i=numOfElements; i>=0; i--) {
            const currentCaseLocator = (i-1).toString();
            const legacyApplication = `#caseMatches_${currentCaseLocator}_${currentCaseLocator} > fieldset > ccd-field-read:nth-child(2) > div > ccd-field-read-label > div > dl > dd`;

            await this.page.waitForTimeout(testConfig.CaseMatchesLocateRemoveButtonDelay);
            /* eslint-disable no-await-in-loop */
            const text = await this.page.locator(legacyApplication)
                .filter({hasText: legacyApplicationTypeText})
                .textContent();

            if (text === legacyApplicationTypeText) {
                // eslint-disable-next-line no-unused-vars
                if (!testConfig.TestAutoDelayEnabled) {
                    /* eslint-disable no-await-in-loop */
                    await this.page.waitForTimeout(testConfig.ManualDelayShort);
                }
                const caseMatchesValidYesLocatorNew = `#caseMatches_${currentCaseLocator}_valid_Yes`;
                this.caseMatchesImportLocatorNew = this.page.locator(`#caseMatches_${currentCaseLocator}_doImport_No`);
                /* eslint-disable no-await-in-loop */
                await expect(this.page.locator(caseMatchesValidYesLocatorNew)).toBeVisible();
                /* eslint-disable no-await-in-loop */
                await this.page.locator(caseMatchesValidYesLocatorNew).scrollIntoViewIfNeeded();
                /* eslint-disable no-await-in-loop */
                await expect(this.page.locator(caseMatchesValidYesLocatorNew)).toBeEnabled();
                /* eslint-disable no-await-in-loop */
                await this.page.locator(caseMatchesValidYesLocatorNew).click();
                /* eslint-disable no-await-in-loop */
                await expect(this.caseMatchesImportLocatorNew).toBeVisible();
                /* eslint-disable no-await-in-loop */
                await this.caseMatchesImportLocatorNew.click();
                break;
            }
        }
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
        await this.page.waitForTimeout(testConfig.CaseMatchesCompletionDelay);
    }

    async verifyProbateManCcdCaseNumber() {
        const probateManCaseUrlXpath = '//span[contains(text(),\'print/probateManTypes/GRANT_APPLICATION/cases\')]';

        let caseUrl = await this.page.locator(probateManCaseUrlXpath)
            .first()
            .textContent();

        if (caseUrl.includes('ccd-api-gateway-web')) {
            caseUrl = caseUrl.replace(/http(s?):\/\/.*?\/print/, testConfig.TestBackOfficeUrl + '/print');
        }

        await this.page.goto(caseUrl);
        await this.page.waitForTimeout(testConfig.ManualDelayMedium);
        await expect(this.probateManPrint_waitForText).toBeVisible();
        // this.amOnLoadedPage(caseUrl);
        // await I.wait(testConfig.ManualDelayMedium);
        // await I.waitForText('Grant Application', 600);
        const ccdCaseNoTextXpath = 'xpath=/html/body/pre/table/tbody/tr[3]/td[1]'; // //td[text()='Ccd Case No:']
        const ccdCaseNoText = await this.page.locator(ccdCaseNoTextXpath).textContent();
        const ccdCaseNoValueXpath = 'xpath=/html/body/pre/table/tbody/tr[3]/td[2]';
        if (ccdCaseNoText === 'Ccd Case No:') {
            await expect(this.page.locator(ccdCaseNoValueXpath)).toBeVisible();
        } else {
            // eslint-disable-next-line no-undef
            throw new Exception(`Ccd Case No: text xpath changed on probate man case url ${caseUrl} Page, please verify and update both text and value locators`);
        }
    }

    async enterEventSummary(caseRef, nextStepName) {
        await this.page.waitForTimeout(testConfig.EventSummaryDelay);
        let eventSummaryPrefix = nextStepName;
        await expect(this.page.getByText(nextStepName)).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';
        await expect(this.summaryLocator).toBeEnabled();
        await this.summaryLocator.fill(eventSummaryPrefix + eventSummaryConfig.summary);
        await this.descriptionLocator.fill(eventSummaryPrefix + eventSummaryConfig.comment);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async uploadDocument(caseRef, documentUploadConfig) {
        await expect(this.page.getByRole('heading', {name: documentUploadConfig.waitForText, exact: true})).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.addNewLocator).toBeEnabled();
        await this.addNewLocator.focus();
        await this.addNewLocator.click();
        if (!testConfig.TestAutoDelayEnabled) {
            await this.page.waitForTimeout(testConfig.ManualDelayShort);
        }
        await expect(this.page.locator(`${documentUploadConfig.id}_0_Comment`)).toBeVisible();
        await this.page.waitForTimeout(2);
        await this.page.locator(`${documentUploadConfig.id}_0_Comment`).fill(documentUploadConfig.comment);
        await this.page.waitForTimeout(1);
        if (!testConfig.TestAutoDelayEnabled) {
            await this.page.waitForTimeout(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
        }

        await expect(this.page.locator(`${documentUploadConfig.id}_0_Comment`)).toHaveValue(documentUploadConfig.comment);
        await expect(this.page.locator(`${documentUploadConfig.id}_0_DocumentType`)).toBeVisible();
        await this.page.locator(`${documentUploadConfig.id}_0_DocumentType`).selectOption(documentUploadConfig.documentType[0]);
        await expect(this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)).toBeVisible();
        await expect(this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)).toBeEnabled();
        await this.page.waitForTimeout(3);
        await this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`).setInputFiles(`${documentUploadConfig.fileToUploadUrl}`);
        await this.waitForUploadToBeCompleted();
        await this.page.waitForTimeout(testConfig.DocumentUploadDelay);

        if (documentUploadConfig.documentType) {
            for (let i = 0; i < documentUploadConfig.documentType.length; i++) {
                const optText = await this.page.locator(`${documentUploadConfig.id}_0_DocumentType option:nth-child(${i+2})`).innerText();
                if (optText !== documentUploadConfig.documentType[i]) {
                    console.info('document upload doc types not as expected.');
                    console.info(`expected: ${documentUploadConfig.documentType[i]}, actual: ${optText}`);
                    console.info('doctype select html:');
                    // eslint-disable-next-line no-await-in-loop
                    console.info(await this.page.locator (`${documentUploadConfig.id}_0_DocumentType`).all());
                }
                console.info('Document upload type number ' + (i+1) + ' in list - ' + documentUploadConfig.documentType[i]);
                assert(optText === documentUploadConfig.documentType[i]);
            }
        }
        await expect(this.page.locator(`${documentUploadConfig.id}_0_DocumentLink`)).toBeVisible();
        await this.page.waitForTimeout(3);
        await expect(this.page.locator(`${documentUploadConfig.id}_0_Comment`)).toHaveValue(documentUploadConfig.comment);
        // small delay to allow hidden vars to be set
        await this.page.waitForTimeout(testConfig.DocumentUploadDelay);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async uploadLegalStatement(caseRef, documentUploadConfig) {
        await expect(this.page.getByRole('heading', {name: documentUploadConfig.legalStatement_waitForText, exact: true})).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.page.locator(`${documentUploadConfig.uploadLegalStatementId}`)).toBeVisible();
        await expect(this.page.locator(`${documentUploadConfig.uploadLegalStatementId}`)).toBeEnabled();
        await this.page.waitForTimeout(3);
        await this.page.locator(`${documentUploadConfig.uploadLegalStatementId}`).setInputFiles(`${documentUploadConfig.fileToUploadUrl}`);
        await this.waitForUploadToBeCompleted();
        await this.page.waitForTimeout(testConfig.DocumentUploadDelay);
    }

    async emailCaveator(caseRef) {
        await expect(this.emailCaveatorHeadingLocator).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.emailLocator).toBeEnabled();
        await this.emailLocator.fill(emailCaveatorConfig.email_message_content);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async reopenCaveat(caseRef) {
        await expect(this.reopenCaveatHeadingLocator).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.caveatReopenReasonLocator).toBeEnabled();
        await this.caveatReopenReasonLocator.fill(reopenCaveatConfig.reopen_caveat_reason);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async withdrawCaveatPage1() {
        await expect(this.withdrawCaveatHeadingLocator).toBeVisible();
        await expect(this.emailRequestedLocator).toBeEnabled();
        await this.emailRequestedLocator.focus();
        await this.emailRequestedLocator.check();
        await expect(this.bulkPrintTextLocator).toBeVisible();
        await this.bulkPrintOptionLocator.focus();
        await this.bulkPrintOptionLocator.check();
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async registrarsDecision(caseRef) {
        await expect(this.registrarDecisionHeadingLocator).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.registrarDecisionSelectionLocator).toBeEnabled();
        await this.registrarDecisionSelectionLocator.focus();
        await this.registrarDecisionSelectionLocator.click();
        await this.page.waitForTimeout(3);
        await this.registrarDecisionSelectionLocator.click();
        await this.registrarDecisionReasonLocator.fill(registrarsDecisionConfig.furtherInformation);

        await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

    async handleEvidence(caseRef, handled ='No') {
        await expect(this.handleEvidenceHeadingLocator).toBeVisible();
        this.caseRefLocator = this.page.getByRole('heading', {name: `${caseRef}`});
        this.handleEvidenceChkBoxOptionLocator = this.page.locator(`#evidenceHandled_${handled}`);
        await expect(this.caseRefLocator).toBeVisible();
        await expect(this.handleEvideChkBoxLocator).toBeEnabled();
        await this.handleEvidenceChkBoxOptionLocator.isChecked();
        await this.handleEvideChkBoxLocator.click();
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async caseProgressStopEscalateIssueAddCaseStoppedReason() {
        await expect(this.addCaseStopReasonLocator).toBeEnabled();
        await this.addCaseStopReasonLocator.click();
        await expect(this.caseStopReasonLocator).toBeEnabled();
        await this.caseStopReasonLocator.click();
        await this.caseStopReasonLocator.selectOption({label: `${createCaseConfig.stopReason}`});
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async chooseResolveStop(resolveStop) {
        await expect.soft(this.resolveStopLocator).toBeEnabled();
        await this.resolveStopLocator.selectOption({label: `${resolveStop}`});
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async issueGrant(caseRef) {
        await expect(this.issueGrantHeadingLocator).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await expect(this.bulkPrintLocator).toBeEnabled();
        await this.bulkPrintLocator.click();
        await expect(this.emailGrantIssueNotificationLocator).toBeEnabled();
        await this.emailGrantIssueNotificationLocator.click();
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async enterNewDob(updatedDoB) {
        await expect(this.page.getByRole('heading', {name: newConfig.waitForText})).toBeVisible();
        await expect(this.newDobLocator).toBeEnabled();
        await this.newDobLocator.fill(updatedDoB);
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async chooseNewState(newState) {
        await expect(this.page.getByRole('heading', {name: newConfig.newState_waitForText})).toBeVisible();
        await expect(this.newStateLocator).toBeEnabled();
        await this.newStateLocator.selectOption({label: `${newState}`});
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }

    async selectWithdrawalReason(caseRef, withdrawalConfig) {
        await expect(this.page.getByText(withdrawalConfig.waitForText)).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await this.willWithdrawReasonLocator.selectOption({label: `${withdrawalConfig.list1_text}`});
        await this.waitForNavigationToComplete(commonConfig.continueButton);
        // await I.waitForText(withdrawalConfig.waitForText, testConfig.WaitForTextTimeout);

        // await I.see(caseRef);

        // await I.selectOption('#withdrawalReason', withdrawalConfig.list1_text);

        // await I.waitForNavigationToComplete(commonConfig.continueButton);
    }
};
