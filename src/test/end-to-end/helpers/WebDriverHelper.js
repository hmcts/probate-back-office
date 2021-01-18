'use strict';

const Helper = codecept_helper;
const testConfig = require('src/test/config.js');

class WebDriverHelper extends Helper {

    async clickBrowserBackButton() {
        const browser = this.helpers.WebDriver.browser;

        await browser.back();
    }

    /**
     * waits for naigation to complete, optionally provide a button to click
     * to start the navigation
     * @param {object} locator - a locator to a button to click, or null 
     * @param {number} webDriverWait - optional - a wait time - defaults to 3 if not provided
     * @returns {object} - Promise
     */
    async waitForNavigationToComplete(locator, webDriverWait=3) {
        const helper = this.helpers.WebDriver;

        if (locator) {
            // must be a button to click
            await helper.waitForClickable(locator, testConfig.TestTimeToWaitForText);
            await helper.click(locator);
        }

        // so for ie11 / selenium webdriver this isn't that reliable,
        // is best combined with JSWaits amOnLoadedPage in next page
        await helper.wait(webDriverWait);
    }

    async downloadPdfIfNotIE11(pdfLink) {
        const browserName = this.helpers.WebDriver.config.browser;
        const helper = this.helpers.WebDriver;

        if (browserName !== 'internet explorer') {
            await helper.click(pdfLink);
        }
    }

    async uploadDocumentIfNotMicrosoftEdge() {
        const browserName = this.helpers.WebDriver.config.browser;
        const helper = this.helpers.WebDriver;

        if (browserName !== 'MicrosoftEdge') {
            await helper.waitForElement('.dz-hidden-input', testConfig.TestTimeToWaitForText * testConfig.TestOneMilliSecond);
            await helper.attachFile('.dz-hidden-input', testConfig.TestDocumentToUpload);
            await helper.waitForEnabled('#button', testConfig.TestTimeToWaitForText);
        }
    }

    async runAccessibilityTest() {
        //Ignore this for web driver
        await Promise.resolve();
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
            helper.click(tabTitle);
        }
    }
}
module.exports = WebDriverHelper;
