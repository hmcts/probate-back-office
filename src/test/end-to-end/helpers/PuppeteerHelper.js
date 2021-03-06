'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';
const testConfig = require('src/test/config.js');

const {runAccessibility} = require('./accessibility/runner');

class PuppeteerHelper extends Helper {

    async clickBrowserBackButton() {
        const page = this.helpers[helperName].page;
        await page.goBack();
    }

    async waitForNavigationToComplete(locator) {
        const page = this.helpers[helperName].page;
        const promises = [];
        if (!testConfig.TestForXUI) {
            promises.push(page.waitForNavigation({timeout: 240000, waitUntil: ['domcontentloaded', 'networkidle0']})); // The promise resolves after navigation has finished
        }

        if (locator) {
            promises.push(page.click(locator));
        }
        await Promise.all(promises);
    }

    async clickTab(tabTitle) {
        const helper = this.helpers[helperName];
        if (testConfig.TestForXUI) {
            const tabXPath = `//div[text()='${tabTitle}']`;

            // wait for element defined by XPath appear in page
            await helper.page.waitForXPath(tabXPath);

            // evaluate XPath expression of the target selector (it return array of ElementHandle)
            const clickableTab = await helper.page.$x(tabXPath);

            await helper.page.evaluate(el => el.click(), clickableTab[0]);
        } else {
            await helper.click(tabTitle);
        }
    }

    replaceAll(string, search, replace) {
        if (!string) {
            return null;
        }
        return string.split(search).join(replace);
    }

    htmlEquals(html1, html2) {
        if ((html1 && !html2) || (html2 && !html1)) {
            return false;
        }
        if (!html1 && !html2) {
            return true;
        }
        return this.replaceAll(this.replaceAll(this.replaceAll(html1, '-c16'), '-c17'), '-c18') ===
            this.replaceAll(this.replaceAll(this.replaceAll(html2, '-c16'), '-c17'), '-c18');
    }

    async navigateToPage(url) {
        await this.amOnPage(url);
        await this.waitForNavigationToComplete();
    }

    async downloadPdfIfNotIE11(pdfLink) {
        const helper = this.helpers[helperName];
        await helper.click(pdfLink);
    }

    async uploadDocumentIfNotMicrosoftEdge() {
        const helper = this.helpers[helperName];
        await helper.waitForElement('.dz-hidden-input', testConfig.TestTimeToWaitForText * testConfig.TestOneMilliSecond);
        await helper.attachFile('.dz-hidden-input', testConfig.TestDocumentToUpload);
        await helper.waitForEnabled('#button', testConfig.TestTimeToWaitForText);
    }

    async performAsyncActionForElements(locator, actionFunc) {
        const elements = await this.helpers.Puppeteer._locate(locator);
        if (!elements || elements.length === 0) {
            return;
        }
        for (let i = 0; i < elements.length; i++) {
            // eslint-disable-next-line no-await-in-loop
            await actionFunc(elements[i]);
        }
    }

    async runAccessibilityTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        const url = await this.helpers[helperName].grabCurrentUrl();
        const {page} = await this.helpers[helperName];

        runAccessibility(url, page);
    }
}
module.exports = PuppeteerHelper;
