'use strict';

const createGrantOfProbateConfig = require('./createGrantOfProbateManualProbateManCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForElement(`#ihtFormCompletedOnline_${createGrantOfProbateConfig.IhtFormCompletedOnlineYes}`);
        await I.click(`#ihtFormCompletedOnline_${createGrantOfProbateConfig.IhtFormCompletedOnlineYes}`);
        await I.click(`#foreignAsset_${createGrantOfProbateConfig.ForeignAssetNo}`);
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
