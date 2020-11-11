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
        /*
        if (locator) {
            await page.click(locator);
        }
        return await page.waitForNavigation({waitUntil: ['domcontentloaded', 'networkidle0']});
        */

        const promises = [
            page.waitForNavigation({ waitUntil: ['domcontentloaded', 'networkidle0'] })
        ];

        if (locator) {
            promises.push(page.click(locator));
        }

        return await Promise.all(promises);
    }

    async navigateToPage(url) {
        // const page = this.helpers[helperName].page;
        // await page.goto(url);
        await this.amOnPage(url);
        return await this.waitForNavigationToComplete();
    }

    replaceAll(string, search, replace) {
        if (!string) {
            return null;
        }
        return string.split(search).join(replace);
    }

    /**
     * Converts 1 based month to 3 char text field - e.g. Sep, Oct etc
     * @param {number} month 
     */
    convertMonthToText(month) {
        switch (month) {
            case 1:
                return 'Jan';
            case 2:
                return 'Feb';
            case 3:
                return 'Mar';
            case 4:
                return 'Apr';
            case 5:
                return 'May';
            case 6:
                return 'Jun';
            case 7:
                return 'Jul';
            case 8:
                return 'Aug';
            case 9:
                return 'Sep';
            case 10:
                return 'Oct';
            case 11:
                return 'Nov';
            case 12:
                return 'Dec';
        }
        return null;
    }


    htmlEquals(html1, html2) {
        if ((html1 && !html2) || (html2 && !html1)) {
            return false;
        }
        if (!html1 && !html2) {
            return true;
        }
        return this.replaceAll(this.replaceAll(this.replaceAll(html1, '-c16'), '-c17'), '-c18') ===
            this.replaceAll(this.replaceAll(this.replaceAll(html2, '-c16'), '-c17'), '-c18') ? true : false;
    }
}
module.exports = PuppeteerHelper;
