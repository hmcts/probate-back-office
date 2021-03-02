'use strict';

const testConfig = require('src/test/config');
const createGrantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, forAmendOfSolicitorCreatedCase) {

    const I = this;

    if (crud === 'create') {

        await I.waitForClickable({css: '#titleAndClearingType-TCTNoT'});
        await I.click({css: '#titleAndClearingType-TCTNoT'});
        await I.fillField('#titleAndClearingTypeNoT', createGrantOfProbateConfig.page3_titleAndClearingTypeNoT);

        await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
        await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

        await I.waitForElement('#trustCorpName');
        await I.fillField('#trustCorpName', createGrantOfProbateConfig.page3_nameOfTrustCorp);

        await I.fillField('#lodgementAddress', createGrantOfProbateConfig.page3_lodgementAddress);
        await I.fillField('#lodgementDate-day', createGrantOfProbateConfig.page3_lodgementDate_day);
        await I.fillField('#lodgementDate-month', createGrantOfProbateConfig.page3_lodgementDate_month);
        await I.fillField('#lodgementDate-year', createGrantOfProbateConfig.page3_lodgementDate_year);
        await I.waitForNavigationToComplete(commonConfig.continueButton);

    }



};
