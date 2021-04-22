'use strict';

const grantOfProbateConfig = require('./createGrantOfProbateConfig');
const solGrantOfProbateConfig = require('src/test/end-to-end/pages/solicitorApplyProbate/grantOfProbate/grantOfProbate');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(grantOfProbateConfig.amendCaseDetails_waitForText);
    await I.selectOption('#selectionList', grantOfProbateConfig.amendApplicantDetails_update_option);
    await I.waitForNavigationToComplete(commonConfig.continueButton);

    // check visibility (schemaVersion makes visible)
    await I.waitForVisible({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    // check values set correctly
    await I.seeCheckboxIsChecked({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
