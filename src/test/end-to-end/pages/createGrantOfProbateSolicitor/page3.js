'use strict';

const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    //currently do nothing on update
    if (crud === 'update' || createGrantOfProbateConfig.page1_paperForm === 'Yes') {
        return;
    }

    if (crud === 'create') {

        const tct = createGrantOfProbateConfig.page3_titleAndClearingTypeValue;

        await I.waitForClickable({css: `#titleAndClearingType-${tct}`});
        await I.click({css: `#titleAndClearingType-${tct}`});

        // cater for other titleAndClearingType options as tests are added
        if (tct === 'TCTTrustCorpResWithApp') {

            await I.waitForVisible({css: '#trustCorpName'});
            await I.fillField('#trustCorpName', createGrantOfProbateConfig.page3_nameOfTrustCorp);

            await I.click(createGrantOfProbateConfig.page3_trustCorpPostcodeLink);

            await I.fillField('#trustCorpAddress_AddressLine1', createGrantOfProbateConfig.page3_trustAddress_line1);
            await I.fillField('#trustCorpAddress_AddressLine2', createGrantOfProbateConfig.page3_trustAddress_line2);
            await I.fillField('#trustCorpAddress_PostTown', createGrantOfProbateConfig.page3_trustAddress_town);
            await I.fillField('#trustCorpAddress_PostCode', createGrantOfProbateConfig.page3_trustAddress_postcode);
            await I.fillField('#trustCorpAddress_Country', createGrantOfProbateConfig.page3_trustAddress_country);

        } else if (tct.indexOf('Succ') >= 0) {
            await I.waitForVisible({css: '#nameOfFirmNamedInWill'});
            await I.fillField({css: '#nameOfFirmNamedInWill'}, createGrantOfProbateConfig.page3_nameOfFirmNamedInWill);
            await I.fillField({css: '#nameOfSucceededFirm'}, createGrantOfProbateConfig.page3_nameOfSucceededFirm);

            await I.click(createGrantOfProbateConfig.page3_addressOfSucceededFirmPostcodeLink);

            await I.scrollTo('#addressOfSucceededFirm_AddressLine1');
            await I.fillField('#addressOfSucceededFirm_AddressLine1', createGrantOfProbateConfig.page3_succeededAddress_line1);
            await I.fillField('#addressOfSucceededFirm_AddressLine2', createGrantOfProbateConfig.page3_succeededAddress_line2);
            await I.fillField('#addressOfSucceededFirm_PostTown', createGrantOfProbateConfig.page3_succeededAddress_town);
            await I.fillField('#addressOfSucceededFirm_PostCode', createGrantOfProbateConfig.page3_succeededAddress_postcode);
            await I.fillField('#addressOfSucceededFirm_Country', createGrantOfProbateConfig.page3_succeededAddress_country);

            await I.click({css: '#morePartnersHoldingPowerReserved-No'});
        }

        if (tct === 'TCTPartSuccPowerRes' || tct === 'TCTPartPowerRes' || tct === 'TCTSolePrinSucc' || tct === 'TCTSolePrin' ||
            tct === 'TCTPartSuccAllRenouncing' || tct === 'TCTPartAllRenouncing' || tct === 'TCTPartSuccOthersRenouncing' ||
            tct === 'TCTPartOthersRenouncing' || tct === 'TCTNoT') {
            //make sure both immediately visible
            await I.waitForVisible('#whoSharesInCompanyProfits-partner');

            await I.scrollTo('#whoSharesInCompanyProfits-partner');
            await I.click('#whoSharesInCompanyProfits-partner');
            await I.click('#whoSharesInCompanyProfits-member');
        } else {
            // make sure fields are hidden
            const numEls = await I.grabNumberOfVisibleElements({css: '#whoSharesInCompanyProfits-Partners'});
            assert (numEls === 0);
        }
    }

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
