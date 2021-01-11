class JSWait extends codecept_helper {

    async navByClick (text, locator, webDriverWait) {
        const helper = this.helpers.WebDriverIO || this.helpers.Puppeteer;
        const helperIsPuppeteer = this.helpers.Puppeteer;

        if (helperIsPuppeteer) {
            await Promise.all([
                helper.page.waitForNavigation({waitUntil: ['domcontentloaded', 'networkidle0']}),
                locator ? helper.click(text, locator) : helper.click(text)
            ]);
            return;
        }
        // non Puppeteer
        return Promise.all([
            locator ? helper.click(text, locator) : helper.click(text),
            // needs to be combined with amOnLoadedPage in the next page really as it may be more than 3 secs
            helper.wait(webDriverWait ? webDriverWait : 3)
        ]);
    }

    async amOnLoadedPage (url) {
        const helper = this.helpers.WebDriverIO || this.helpers.Puppeteer;
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
