'use strict';

const testConfig = require('src/test/config.js');

module.exports = async function (caseType) {

    const I = this;

    const probateManCaseUrlXpath = {xpath: '//span[contains(text(),\'print/probateManTypes/GRANT_APPLICATION/cases\')]'};

    let caseUrl = await I.grabTextFrom(probateManCaseUrlXpath);
    const caseTypeNoSpace = await I.replaceAll(caseType,' ','');

    if (caseUrl.includes('ccd-api-gateway-web')) {
        caseUrl = caseUrl.replace(/http(s?):\/\/.*?\/print/, testConfig.TestBackOfficeUrl +
            'PROBATE/' + caseTypeNoSpace + '/print');
    }

    I.amOnLoadedPage(caseUrl);
    await I.wait(testConfig.ManualDelayMedium);
    await I.waitForText('Grant Application', 600);
    const ccdCaseNoTextXpath = {xpath: '/html/body/pre/table/tbody/tr[3]/td[1]'}; // //td[text()='Ccd Case No:']
    const ccdCaseNoText = await I.grabTextFrom(ccdCaseNoTextXpath);
    const ccdCaseNoValueXpath = {xpath: '/html/body/pre/table/tbody/tr[3]/td[2]'};
    if (ccdCaseNoText === 'Ccd Case No:') {
        await I.see('', ccdCaseNoValueXpath);
    } else {
        // eslint-disable-next-line no-undef
        throw new Exception(`Ccd Case No: text xpath changed on probate man case url ${caseUrl} Page, please verify and update both text and value locators`);
    }
};
