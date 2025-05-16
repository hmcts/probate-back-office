const base = require('@playwright/test');
const {BasePage} = require('../Pages/utility/basePage');
const {SignInPage} = require('../Pages/IDAM/signIn');
const {CreateCasePage} = require('../Pages/newCase/newCase');
const {CwEventActionsPage} = require('../Pages/newCase/cwEventActions');
const {SolCreateCasePage} = require('../Pages/newCase/solNewCase');

exports.test = base.test.extend({
    basePage: async ({page}, use) => {
        await use(new BasePage(page));
    },

    signInPage: async ({page}, use) => {
        await use(new SignInPage(page));
    },

    createCasePage: async ({page}, use) => {
        await use(new CreateCasePage(page));
    },

    cwEventActionsPage: async ({page}, use) => {
        await use(new CwEventActionsPage(page));
    },

    solCreateCasePage: async ({page}, use) => {
        await use(new SolCreateCasePage(page));
    },

});
exports.expect = base.expect;
