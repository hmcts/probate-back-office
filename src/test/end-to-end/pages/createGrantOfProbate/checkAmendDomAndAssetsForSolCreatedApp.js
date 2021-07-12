'use strict';

const grantOfProbateConfig = require('./createGrantOfProbateConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {

    const I = this;

    await I.waitForText(grantOfProbateConfig.amendCaseDetails_waitForText);
    await I.selectOption('#selectionList', grantOfProbateConfig.domAndAssets_update_option);
    await I.waitForNavigationToComplete(commonConfig.continueButton);

    // check applicationGrounds invisible (schemaVersion makes invisible)
    await I.waitForInvisible({css: '#applicationGrounds'});
    await I.waitForVisible({css: '#furtherEvidenceForApplication'});

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
