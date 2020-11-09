'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';

class PuppeteerHelper extends Helper {

    async clickBrowserBackButton() {
        const page = this.helpers[helperName].page;

        return await page.goBack();
    }

    async waitForNavigationToComplete(locator) {
        const page = this.helpers[helperName].page;
        const promises = [
            page.waitForNavigation({waitUntil: ['domcontentloaded', 'networkidle0']}), // The promise resolves after navigation has finished
        ];

        if (locator) {
            promises.push(page.click(locator));
        }

        await Promise.all(promises);
    }

    async navigateToPage(url) {
        const page = this.helpers[helperName].page;
        await Promise.all([
            //await page.goto(url),
            await page.waitForNavigation({waitUntil: ['domcontentloaded', 'networkidle0']})
        ]);
    }

}
module.exports = PuppeteerHelper;
