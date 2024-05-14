const base = require('@playwright/test');
const { BasePage } = require('../Pages/utility/basePage');
const { SignInPage } = require('../Pages/IDAM/signIn');
const { CreateCasePage } = require('../Pages/newCase/newCase');

//const { NewCaveatPage } = require('../Pages/newCase/newCase');

exports.test = base.test.extend({
    /*page: async ({ baseURL, page }, use) => {
        await page.goto(baseURL);
        await use(page);
    },*/

    basePage: async ({page}, use) => {
        await use(new BasePage(page));
    },

    signInPage: async ({page}, use) => {
        await use(new SignInPage(page));
    },

    createCasePage: async ({page}, use) => {
        await use(new CreateCasePage(page));
    },

 /*   newCaveatPage: async ({page}, use) => {
        await use(new NewCaveatPage(page));
    },*/
});
exports.expect = base.expect;
