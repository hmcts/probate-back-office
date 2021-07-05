const {getAccessibilityTestResult} = require('./accessibility/runner');
const {generateAccessibilityReport} = require('../../reporter/customReporter');
const testConfig = require('src/test/config.js');

class JSWait extends codecept_helper {

    _finishTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        generateAccessibilityReport(getAccessibilityTestResult());
    }

    async amOnLoadedPage (url) {
        const helper = this.helpers.WebDriver || this.helpers.Puppeteer;
        const helperIsPuppeteer = this.helpers.Puppeteer;

        if (helperIsPuppeteer) {
            if (url.indexOf('http') !== 0) {
                url = helper.options.url + url;
            }

            await Promise.all([
                // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
                helper.page.waitForNavigation({waitUntil: ['domcontentloaded', 'networkidle0']}, 60), // The promise resolves after navigation has finished
                helper.page.goto(url)
            ]);
        } else {
            // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
            await helper.amOnPage(url, 60);
            await helper.waitInUrl(url, 60);
        }
    }
}

module.exports = JSWait;
