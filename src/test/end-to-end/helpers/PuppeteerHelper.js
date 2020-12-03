'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';

class PuppeteerHelper extends Helper {

    clickBrowserBackButton() {
        const page = this.helpers[helperName].page;

        return page.goBack();
    }

    async waitForNavigationToComplete(locator) {
        const page = this.helpers[helperName].page;
        const promises = [
            page.waitForNavigation({ waitUntil: ['domcontentloaded', 'networkidle0'] }) // The promise resolves after navigation has finished
        ];

        if (locator) {
            promises.push(page.click(locator));
        }
        return await Promise.all(promises);
    }
}

module.exports = PuppeteerHelper;
