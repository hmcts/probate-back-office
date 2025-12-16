const {getAccessibilityTestResult} = require('./accessibility/runner');
const {generateAccessibilityReport} = require('../../reporter/customReporter');
const testConfig = require('src/test/config.cjs');

class JSWait extends codecept_helper {

    _finishTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        generateAccessibilityReport(getAccessibilityTestResult());
    }

    async delay(time) {
        await new Promise(function (resolve) {
            setTimeout(resolve, time * 1000);
        });
    }

    async amOnLoadedPage (url) {
        const helper = this.helpers.WebDriver || this.helpers.Playwright;
        const helperIsPlaywright = this.helpers.Playwright;

        if (helperIsPlaywright) {
            if (url.indexOf('http') !== 0) {
                url = helper.options.url + url;
            }
        }
        // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
        await helper.amOnPage(url, 60);
        await helper.waitInUrl(url, 60);
    }

    async amOnPage (url) {
        const helper = this.helpers.WebDriver || this.helpers.Playwright;
        const helperIsPlaywright = this.helpers.Playwright;

        if (helperIsPlaywright) {
            if (url.indexOf('http') !== 0) {
                url = helper.options.url + url;
            }
        }
        // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
        await helper.amOnPage(url, 60);
        await helper.waitForNavigationToComplete();
    }
    async checkForText(text, timeout = null) {
        const helper = this.helpers.WebDriver || this.helpers.Playwright;
        try {
            await helper.waitForText(text, timeout);
        } catch (e) {
            console.log(`Text "${text}" not found on page.`);
            return false;
        }
        return true;
    }
}

module.exports = JSWait;
