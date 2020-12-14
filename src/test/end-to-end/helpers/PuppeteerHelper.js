'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';
const testConfig = require('src/test/config.js');

class PuppeteerHelper extends Helper {

    async clickBrowserBackButton() {
        const page = this.helpers[helperName].page;
        await page.goBack();
    }

    async waitForNavigationToComplete(locator) {
        const page = this.helpers[helperName].page;
        const promises = [];

        if (!testConfig.TestForXUI) {
            promises.push(page.waitForNavigation({timeout: 60000, waitUntil: ['domcontentloaded', 'networkidle0']})); // The promise resolves after navigation has finished
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
            const clickableTab = await helper.page.$x(tabXPath);
            await clickableTab[0].click();
        } else {
            helper.click(tabTitle);
        }
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
}
module.exports = PuppeteerHelper;
