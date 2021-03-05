'use strict';

const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {

        const tct = createGrantOfProbateConfig.page3_titleAndClearingTypeValue;

        await I.waitForClickable({css: `#titleAndClearingType-${tct}`});
        await I.click({css: `#titleAndClearingType-${tct}`});

        // cater for other titleAndClearingType options as tests are added
        if (tct === 'TCTTrustCorpResWithApp') {

            await I.waitForVisible({css: '#trustCorpName'});
            await I.fillField('#trustCorpName', createGrantOfProbateConfig.page3_nameOfTrustCorp);

            await I.fillField('#lodgementAddress', createGrantOfProbateConfig.page3_lodgementAddress);
            await I.fillField('#lodgementDate-day', createGrantOfProbateConfig.page3_lodgementDate_day);
            await I.fillField('#lodgementDate-month', createGrantOfProbateConfig.page3_lodgementDate_month);
            await I.fillField('#lodgementDate-year', createGrantOfProbateConfig.page3_lodgementDate_year);

        } else if (tct === 'TCTPartSuccPowerRes') {
            await I.waitForVisible({css: '#nameOfFirmNamedInWill'});
            await I.fillField({css: '#nameOfFirmNamedInWill'}, createGrantOfProbateConfig.page3_nameOfFirmNamedInWill);
            await I.fillField({css: '#nameOfSucceededFirm'}, createGrantOfProbateConfig.page3_nameOfSucceededFirm);
            await I.click({css: '#morePartnersHoldingPowerReserved-No'});
        }

        if (tct === 'TCTPartSuccPowerRes' || tct === 'TCTPartPowerRes' || tct === 'TCTSolePrinSucc' || tct === 'TCTSolePrin' ||
            tct === 'TCTPartSuccAllRenouncing' || tct === 'TCTPartAllRenouncing' || tct === 'TCTPartSuccOthersRenouncing' ||
            tct === 'TCTPartOthersRenouncing' || tct === 'TCTNoT') {
            //make sure both immediately visible
            await I.waitForVisible('#soleTraderOrLimitedCompany-No');
            await I.waitForVisible('#whoSharesInCompanyProfits-Partners');

            await I.scrollTo('#soleTraderOrLimitedCompany-No');
            await I.click('#soleTraderOrLimitedCompany-No');

            await I.scrollTo('#whoSharesInCompanyProfits-Partners');
            await I.click('#whoSharesInCompanyProfits-Partners');
            await I.click('#whoSharesInCompanyProfits-Members');
        } else {
            // make sure fields are hidden
            let numEls = await I.grabNumberOfVisibleElements({css: '#soleTraderOrLimitedCompany-Yes'});
            assert (numEls === 0);
            numEls = await I.grabNumberOfVisibleElements({css: '#soleTraderOrLimitedCompany-No'});
            assert (numEls === 0);
            numEls = await I.grabNumberOfVisibleElements({css: '#whoSharesInCompanyProfits-Partners'});
            assert (numEls === 0);
        }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);

};
