'use strict';

const testConfig = require('src/test/config.cjs');
const noticeOfChangeConfig = require('../noticeOfChange/noticeOfChangeConfig.json');

module.exports = async function (caseRef, deceasedSurname) {

    const I = this;
    await I.wait(testConfig.CreateCaseDelay);
    await I.waitForText(noticeOfChangeConfig.page3WaitForText, testConfig.WaitForTextTimeout);

    const caseRefNoDashes = await I.replaceAll(caseRef, '-', '');
    await I.see(caseRefNoDashes);
    await I.see(deceasedSurname);
    const checkAffirmationLocator = noticeOfChangeConfig.affirmationLocator;
    const checkNotifyLocator = noticeOfChangeConfig.notifyCheckboxLocator;
    await I.waitForEnabled({css: checkAffirmationLocator});
    await I.click('#affirmation');
    await I.waitForEnabled({css: checkNotifyLocator});
    await I.click('#notifyEveryParty');
    await I.wait(testConfig.CreateCaseDelay);
    await I.click(noticeOfChangeConfig.continueButtonLocator);
};
