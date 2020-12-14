'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (willType = 'WillLeft') {
    const I = this;
    await I.waitForElement('#solsWillType');
    await I.click(`#solsWillType-${willType}`);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
