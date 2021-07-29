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

    async delay(time) {
        await new Promise(function (resolve) {
            setTimeout(resolve, time * 1000);
        });
    }

    async amOnLoadedPage (url) {
        const helper = this.helpers.WebDriver || this.helpers.Puppeteer;
        const helperIsPuppeteer = this.helpers.Puppeteer;

        if (helperIsPuppeteer) {
            if (url.indexOf('http') !== 0) {
                url = helper.options.url + url;
            }

            // With Xui we have an issue where it gets stuck unless you open a new tab for some reason
            const page = helper.page;
            let dummyTab = await helper.browser.newPage();

            await this.delay(0.5);
            await Promise.all([
                // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
                page.waitForNavigation({waitUntil: 'networkidle2'}), // The promise resolves after navigation has finished
                page.goto(url, 60)
            ]);
            await dummyTab.close();
            await this.delay(0.5);            
            dummyTab = await helper.browser.newPage();
            await this.delay(0.75);
            await dummyTab.close();
        } else {
            // wait for a max of 1 min (override default of max 1 sec), but will return as soon as ready within that timeframe
            await helper.amOnPage(url, 60);
        }
        await helper.waitInUrl(url, 60);
    }
}

module.exports = JSWait;
