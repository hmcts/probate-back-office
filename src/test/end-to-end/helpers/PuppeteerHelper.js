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

    async delay(time) {
        await new Promise(function (resolve) {
            setTimeout(resolve, time * 1000);
        });
    }

    async waitForNavigationToComplete(locator, delay = 0) {
        const page = this.helpers[helperName].page;

        await this.delay(delay);
        const promises = [];
        promises.push(page.waitForNavigation());
        if (locator) {
            if (Array.isArray(locator)) {
                for (let i=0; i < locator.length; i++) {
                    // eslint-disable-next-line no-await-in-loop
                    await page.waitForSelector(locator[i] + ':enabled', {visible: true, timeout: 5000});
                    promises.push(page.click(locator[i]));
                }
            } else {
                await page.waitForSelector(locator + ':enabled', {visible: true, timeout: 5000});
                promises.push(page.click(locator));
            }
        }
        await Promise.all(promises);
        await this.delay(delay);
    }

    async clickTab(tabTitle) {
        const helper = this.helpers[helperName];
        const tabXPath = `//div[contains(text(),"${tabTitle}")]`;

        // wait for element defined by XPath appear in page
        await helper.page.waitForXPath(tabXPath);

        // evaluate XPath expression of the target selector (it returns array of ElementHandle)
        const clickableTabs = await helper.page.$x(tabXPath);

        /* eslint-disable no-await-in-loop */
        for (let i=0; i < clickableTabs.length; i++) {
            await helper.page.evaluate(el => el.click(), clickableTabs[i]);
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

    async logInfo(scenarioName, log, caseRef) {
        let ret = String (scenarioName);
        if (log) {
            ret = ret + ' : ' + log;
        }
        if (caseRef) {
            ret = ret + ' : ' + caseRef;
        }
        await console.info(ret);
    }

    async signOut(delay = testConfig.SignOutDelayDefault) {
        await this.waitForNavigationToComplete('nav.hmcts-header__navigation ul li:last-child a', delay);
    }

    // to help with local debugging
    async printPageAsScreenshot(jpgFileName) {
        const page = this.helpers[helperName].page;
        await page.setViewport({width: 1280, height: 960});
        await page.screenshot({
            path: testConfig.TestOutputDir + '/' + jpgFileName + '.jpg',
            type: 'jpeg',
            fullPage: true
        });
    }
}
module.exports = PuppeteerHelper;
