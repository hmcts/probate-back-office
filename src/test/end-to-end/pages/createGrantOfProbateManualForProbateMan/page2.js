'use strict';

const createGrantOfProbateConfig = require('./createGrantOfProbateManualProbateManCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgressStandard/caseProgressConfig');

module.exports = async function (crud) {

    const I = this;

    if (crud === 'create') {
        await I.waitForText(caseProgressConfig.IHT205Label);
        await I.waitForText(caseProgressConfig.IHT400Label);
        await I.waitForText(caseProgressConfig.IHT207Label);
        await I.click({css: `#ihtFormId-${caseProgressConfig.IHT400Option}`});
        await I.click(`#foreignAsset_${createGrantOfProbateConfig.ForeignAssetNo}`);
    }
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
