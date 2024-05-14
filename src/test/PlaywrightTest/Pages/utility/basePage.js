const { expect } = require('@playwright/test');
const { testConfig } = require ('../../Configs/config');
const { accessibilityPage } = require("../../Accessibility/runner");

exports.BasePage = class BasePage {
    constructor(page) {
        this.page = page;
        this.rejectLocator = page.getByRole('button', { name: "Reject analytics cookies" });
        this.continueButtonLocator = page.getByRole('button', { name: "Continue" });
        this.submitButtonLocator = page.getByRole('button', { name: "Submit" });
    }

    async logInfo(scenarioName, log, caseRef){
        let ret = scenarioName;
        if (log) {
            ret = ret + ' : ' + log;
        }
        if (caseRef) {
            ret = ret + ' : ' + caseRef;
        }
        console.info(ret);
    }

    async rejectCookies (){
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

    async getCaseRefFromUrl(){
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

    async waitForNavigationToComplete(locator, delayTime = 0) {
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.continueButtonLocator).toBeVisible();
        await expect(this.continueButtonLocator).toBeEnabled();
        this.continueButtonLocator.click();
        await navigationPromise;
    }

    async waitForSubmitNavigationToComplete(){
        const navigationPromise = this.page.waitForNavigation();
        await expect(this.submitButtonLocator).toBeVisible();
        await expect(this.submitButtonLocator).toBeEnabled();
        this.submitButtonLocator.click();
        await navigationPromise;
    }

    async seeCaseDetails(caseRef, tabConfigFile, dataConfigFile, nextStep, endState, delay = testConfig.CaseDetailsDelayDefault){
        if (tabConfigFile.tabName) {
            await expect(this.page.locator(`//div[contains(text(),"${tabConfigFile.tabName}")]`)).toBeEnabled();
           //  const tabXPath = `//div[contains(text(),"${tabConfigFile.tabName}")]`;
            // Tabs are hidden when there are more tabs
            // await I.waitForElement(tabXPath, tabConfigFile.testTimeToWaitForTab || 60);
        }

        await expect(this.page.getByRole('heading', { name: caseRef })).toBeVisible();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).focus();
        await this.page.getByRole('tab', {name: tabConfigFile.tabName}).click();
        await this.page.waitForTimeout(delay);
        // await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);

        // await I.clickTab(tabConfigFile.tabName);
        // await I.wait(delay);

        // *****Need to comment this until accessibility script is completed*****/
        // await this.page.runAccessibilityTest();

        if (tabConfigFile.waitForText) {
            this.tabLocator = this.page.getByText(tabConfigFile.waitForText);
            await expect(this.tabLocator).toBeVisible();
            // await I.waitForText(tabConfigFile.waitForText, testConfig.WaitForTextTimeout || 60);
        }

        /* eslint-disable no-await-in-loop */
        for (let i = 0; i < tabConfigFile.fields.length; i++) {
            if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== '') {
                await expect(this.page.getByText(`{tabConfigFile.fields[i]}`)).toBeVisible;
                // await I.see(tabConfigFile.fields[i]);
            }
        }

        const dataConfigKeys = tabConfigFile.dataKeys;
        // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
        if (tabConfigFile.tabName === 'Event History') {
            await expect(this.page. getByRole('cell', { name: nextStep, exact: true }).locator('span')).toBeVisible();
            await expect(this.page.getByText(endState)).toBeVisible();

            let eventSummaryPrefix = nextStep;

            eventSummaryPrefix = eventSummaryPrefix.replace(/\s+/g, '_').toLowerCase() + '_';

            // await I.waitForText(nextStep, testConfig.WaitForTextTimeout || 60);
            // await I.waitForText(endState, testConfig.WaitForTextTimeout || 60);

            if (dataConfigKeys) {
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.summary)).toBeVisible();
                await expect(this.page.getByText(eventSummaryPrefix + dataConfigFile.comment)).toBeVisible();
                // await I.waitForText(eventSummaryPrefix + dataConfigFile.summary, testConfig.WaitForTextTimeout || 60);
                // await I.waitForText(eventSummaryPrefix + dataConfigFile.comment, testConfig.WaitForTextTimeout || 60);
            }

        } else if (dataConfigKeys) {
            for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
                await expect(this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]])).toBeVisible();
                // await I.waitForText(dataConfigFile[tabConfigFile.dataKeys[i]], testConfig.WaitForTextTimeout || 60);
            }
        }
    }

    async runAccessibilityTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        const url = await this.page.url();
        const {page} = await this.page;

        accessibilityPage.runAccessibility(url, page);
    }
};
