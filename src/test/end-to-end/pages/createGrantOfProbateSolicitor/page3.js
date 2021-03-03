'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud, createGrantOfProbateConfig) {

    const I = this;

    if (crud === 'create') {

        const tct = createGrantOfProbateConfig.page3_titleAndClearingTypeValue;

        await I.waitForClickable({css: `#titleAndClearingType-${tct}`});
        await I.click({css: `#titleAndClearingType-${tct}`});
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

        await I.waitForNavigationToComplete(commonConfig.continueButton);

    }



};
