const {expect} = require('@playwright/test');
const {testConfig} = require ('../../Configs/config');
const assert = require('assert');
const {checkAccessibility} = require('../../Accessibility/axeUtils');

exports.BasePage = class BasePage {
    constructor(page) {
        this.page = page;
        this.rejectLocator = page.getByRole('button', {name: 'Reject analytics cookies'});
        this.continueButtonLocator = page.getByRole('button', {name: 'Continue'});
        this.saveAndContinueButtonLocator = page.getByRole('button', {name: 'Save and continue'});
        this.submitButtonLocator = page.getByRole('button', {name: 'Submit'});
        this.goButtonLocator = page.getByRole('button', {name: 'Go'});
    }

    async logInfo(scenarioName, log, caseRef) {
        let ret = scenarioName;
        await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
        if (log) {
            ret = ret + ' : ' + log;
        }
        if (caseRef) {
            ret = ret + ' : ' + caseRef;
        }
        console.info(ret);
    }

    async runAccessibilityTest(testInfo) {
        const results = await checkAccessibility(this.page, testInfo);
        expect(results.violations).toEqual([]);
    }

    async rejectCookies () {
        if (testConfig.RejectCookies) {
            try {
                //const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
                const numVisibleCookieBannerEls = await this.rejectLocator.count();
                if (numVisibleCookieBannerEls > 0) {
                    // just reject additional cookies
                    await expect(this.rejectLocator).toBeEnabled();
                    await this.rejectLocator.click();
                    await this.page.waitForTimeout(testConfig.RejectCookieDelay);
                }
            } catch (e) {
                console.error(`error trying to close cookie banner: ${e.message}`);
            }
        }
    }

    async getCaseRefFromUrl() {
        await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
        const url = await this.page.url();
        return url
            .replace('#Event%20History', '')
            .replace('#Case%20Progress', '')
            .split('/')
            .pop()
            .match(/.{4}/g)
            .join('-');
    }

    async waitForNavigationToComplete() {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.continueButtonLocator).toBeVisible();
        await expect(this.continueButtonLocator).toBeEnabled();
        this.continueButtonLocator.click();
        await navigationPromise;
    }

    async waitForSubmitNavigationToComplete(buttonName) {
        const navigationPromise = this.page.waitForNavigation();
        if (buttonName === 'Save and continue') {
            await expect(this.saveAndContinueButtonLocator).toBeVisible();
            await expect(this.saveAndContinueButtonLocator).toBeEnabled();
            this.saveAndContinueButtonLocator.click();
        } else {
            await expect(this.submitButtonLocator).toBeVisible();
            await expect(this.submitButtonLocator).toBeEnabled();
            this.submitButtonLocator.click();
        }
        await navigationPromise;
    }

    async waitForGoNavigationToComplete() {
        await expect(this.goButtonLocator).toBeVisible();
        await expect(this.goButtonLocator).toBeEnabled();
        await Promise.all([
            this.goButtonLocator.waitFor({state: 'visible'}),
            this.goButtonLocator.click(),
            this.goButtonLocator.waitFor({state: 'detached'})
        ]);
    }

    async waitForSignOutNavigationToComplete(signOutLocator) {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.page.locator(`${signOutLocator}`)).toBeVisible();
        await expect(this.page.locator(`${signOutLocator}`)).toBeEnabled();
        this.page.locator(`${signOutLocator}`).click();
        await navigationPromise;
    }

    async seeCaseDetails(testInfo, caseRef, tabConfigFile, dataConfigFile, nextStep, endState, delay = testConfig.CaseDetailsDelayDefault) {
        if (tabConfigFile.tabName && tabConfigFile.tabName !== 'Documents') {
            await expect(this.page.getByLabel(`${tabConfigFile.tabName}`, {exact: true})).toBeVisible();
        }

        await expect(this.page.getByRole('heading', {name: caseRef})).toBeVisible();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).focus();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).click();
        await this.page.waitForTimeout(delay);

        // *****Need to comment this until accessibility script is completed*****/
        await this.runAccessibilityTest(testInfo);

        if (tabConfigFile.waitForText) {
            this.tabLocator = this.page.getByLabel(tabConfigFile.waitForText);
            await expect(this.tabLocator).toBeVisible();
        }

        /* eslint-disable no-await-in-loop */
        for (let i = 0; i < tabConfigFile.fields.length; i++) {
            if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== '') {
                const textCount = this.page.getByText(tabConfigFile.fields[i]);
                if (textCount > 1) {
                    if (tabConfigFile.fields[i] === 'Caveat not matched') {
                        await expect(this.page.getByText(tabConfigFile.fields[i]).nth(2)).toBeVisible();
                    }
                    await expect(this.page.getByText(tabConfigFile.fields[i], {exact: true})).toBeVisible();
                } else if (tabConfigFile.tabName === 'Event History') {
                    await expect(this.page.getByRole('table', {name: 'Details'})).toContainText(tabConfigFile.fields[i]);
                } else {
                    await expect(this.page.getByRole('table', {name: 'case viewer table'})).toContainText(tabConfigFile.fields[i]);
                }
            }
        }

        const dataConfigKeys = tabConfigFile.dataKeys;
        // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
        if (tabConfigFile.tabName === 'Event History') {
            if (nextStep === endState) {
                await expect(this.page.getByText(nextStep).nth(2)).toBeVisible();
                await expect(this.page.getByText(endState).nth(3)).toBeVisible();
            } else if (endState === 'Caveat created' || nextStep === 'Apply for probate' || endState === 'Grant of probate created') {
                await expect(this.page.getByRole('cell', {name: endState, exact: true})).toBeVisible();
                await expect(this.page.getByLabel(nextStep).nth(1)).toBeVisible();
                // await expect(this.page.getByLabel(nextStep), {exact: true}).toBeVisible();
            } else {
                await expect(this.page.getByRole('cell', {name: endState, exact: true}).locator('span')).toBeVisible();
                await expect(this.page.getByRole('cell', {name: nextStep, exact: true}).locator('span')).toBeVisible();
            }
            let eventSummaryPrefix = nextStep;
            eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';
            if (dataConfigKeys && nextStep !== 'Change state') {
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.summary)).toBeVisible();
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.comment)).toBeVisible();
            }
        } else if (dataConfigKeys) {
            for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
                const textCount = this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]]);
                if (textCount > 1) {
                    await expect(this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]], {exact: true})).toBeVisible();
                } else {
                    await expect(this.page.getByRole('table', {name: 'case viewer table'})).toContainText(dataConfigFile[tabConfigFile.dataKeys[i]]);
                }
            }
        }
    }

    async seeUpdatesOnCase(testInfo, caseRef, tabConfigFile, tabUpdates, tabUpdatesConfigFile, forUpdateApplication) {
        await expect(this.page.getByRole('heading', {name: caseRef})).toBeVisible();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).focus();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).click();
        await this.runAccessibilityTest(testInfo);

        if (tabUpdates) {
            const updatedConfig = tabConfigFile[tabUpdates];
            let fields = updatedConfig.fields;
            let keys = updatedConfig.dataKeys;
            if (forUpdateApplication) {
                fields = fields.concat(updatedConfig.updateAppFields);
                keys = keys.concat(updatedConfig.updateAppDataKeys);
            }

            for (let i = 0; i < fields.length; i++) {
                // eslint-disable-next-line
                await expect(this.page.getByText(fields[i]).first()).toBeVisible();
            }

            for (let i = 0; i < keys.length; i++) {
                // eslint-disable-next-line
                const textLocator = this.page.getByText(tabUpdatesConfigFile[keys[i]], {exact: true});
                const locatorCount = await textLocator.count();
                if (locatorCount > 0) {
                    await textLocator.first().isVisible();
                }
            }
        }
    }

    async dontSeeCaseDetails(fieldLabelsNotToBeShown) {
        let visibleElements;
        let numElements;

        for (let i = 0; i < fieldLabelsNotToBeShown.length; i++) {
            // eslint-disable-next-line
            visibleElements = await this.page.locator(`xpath=//div[contains(@class, 'case-viewer-label')][text()='${fieldLabelsNotToBeShown[i]}']`).filter({ visible: true });
            numElements = await visibleElements.count();
            assert (numElements === 0);
        }
    }

    async waitForUploadToBeCompleted() {
        const locs = await this.page.getByText('Cancel upload').all();
        for (let i = 0; i < locs.length; i++) {
            await expect(locs[i]).toBeDisabled();
        }
    }

};
