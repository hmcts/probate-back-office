'use strict';

const grantOfProbateConfig = require('./createGrantOfProbateConfig');
const solGrantOfProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/grantOfProbate/grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(grantOfProbateConfig.amendCaseDetails_waitForText);
    await I.selectOption('#selectionList', grantOfProbateConfig.amendSolicitorDetails_update_option);
    await I.waitForNavigationToComplete(commonConfig.continueButton);

    // check visibility (schemaVersion makes visible)
    await I.waitForVisible({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.waitForVisible({css: '#lodgementAddress'});
    await I.waitForVisible({css: '#lodgementDate-day'});
    await I.waitForVisible({css: '#lodgementDate-month'});
    await I.waitForVisible({css: '#lodgementDate-year'});

    // check values set correctly
    await I.seeCheckboxIsChecked({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    await I.seeInField({css: '#lodgementAddress'}, solGrantOfProbateConfig.page2_lodgementAddress);
    await I.seeInField({css: '#lodgementDate-day'}, solGrantOfProbateConfig.page2_lodgementDate_day);
    await I.seeInField({css: '#lodgementDate-month'}, solGrantOfProbateConfig.page2_lodgementDate_month);
    await I.seeInField({css: '#lodgementDate-year'}, solGrantOfProbateConfig.page2_lodgementDate_year);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
