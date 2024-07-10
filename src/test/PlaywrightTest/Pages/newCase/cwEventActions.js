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
        this.registrarDecisionReasonLocator = this.page.locator(`#registrarDirectionToAdd_furtherInformation`);
    }

    async chooseNextStep(nextStep) {
        await expect(this.nextStepLocator).toBeEnabled();
        await this.nextStepLocator.selectOption(nextStep);
        await this.page.waitForTimeout(testConfig.CaseworkerGoButtonClickDelay);
        await this.waitForGoNavigationToComplete(commonConfig.submitButton);
    }

    async selectCaseMatchesForCaveat(caseRef, nextStepName, retainFirstItem=true, addNewButtonLocator=null, skipMatchingInfo=false) {
        await expect(this.page.getByText(nextStepName)).toBeVisible();
        await expect(this.page.getByText(caseRef)).toBeVisible();
        await this.page.waitForTimeout(testConfig.CaseMatchesInitialDelay);
        const numOfElements = this.btnLocator;
        if (numOfElements > 0) {
            await expect(this.caseMatchLocator).toBeVisible();
            await expect(this.caseMatchValidLocator).toBeVisible();
            // await I.waitForElement('#caseMatches_0_0', testConfig.WaitForTextTimeout);
            // await I.waitForVisible({css: '#caseMatches_0_valid_Yes'}, testConfig.WaitForTextTimeout);
        }
        this.addNewButtonLocator = await this.page.getByText(addNewButtonLocator);
        if (numOfElements === 0 && retainFirstItem && addNewButtonLocator) {
            await this.page.waitForTimeout(testConfig.CaseMatchesAddNewButtonClickDelay);
            await expect(this.addNewButtonLocator).toBeEnabled();
            await this.addNewButtonLocator.click();
        }

        if (retainFirstItem && (numOfElements > 0 || addNewButtonLocator)) {
            // Just a small delay - occasionally we get issues here but only relevant for local dev.
            // Only necessary where we have no auto delay (local dev).
            if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayMedium);
            }
            await expect(this.caseMatchValidLocator).toBeEnabled();
            await this.caseMatchValidLocator.focus();
            await this.caseMatchValidLocator.check();
            await expect(this.caseMatchImportLocator).toBeEnabled();
            await this.caseMatchImportLocator.click();
        }

        await this.page.evaluate(async () => {
            const delay = ms => new Promise(resolve => setTimeout(resolve, ms));
            for (let i = 0; i < document.body.scrollHeight; i += 1000) {
                window.scrollTo(0, i);
                /* eslint-disable no-await-in-loop */
                await delay(100);
            }
        });
        await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
        if (skipMatchingInfo) {
            await expect(this.summaryLocator).toBeVisible();
            if (!testConfig.TestAutoDelayEnabled) {
                await this.page.waitForTimeout(testConfig.ManualDelayShort);
            }
            await this.waitForNavigationToComplete(commonConfig.continueButton);
        }
        await this.page.waitForTimeout(testConfig.CaseMatchesCompletionDelay);
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
        // await this.uploadDocumentLocator.focus();
        // await this.uploadDocumentLocator.click();
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
        //await I.waitForEnabled({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
        //await I.dontSeeCheckboxIsChecked({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
        //await I.click({css: `#registrarDirectionToAdd_decision-${registrarsDecisionConfig.radioProbateRefused}`});
        //await I.fillField('#registrarDirectionToAdd_furtherInformation', registrarsDecisionConfig.furtherInformation);
        await this.waitForNavigationToComplete(commonConfig.continueButton);
        //await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
    }
};
