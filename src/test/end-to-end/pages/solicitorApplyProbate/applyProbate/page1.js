'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const applyProbateConfig = require('./applyProbateConfig');
const testConfig = require('src/test/config');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#solsStartPage');
    await I.waitForText( applyProbateConfig.ApplyForProbateHeading2, testConfig.TestTimeToWaitForText);

    await I.runAccessibilityTest();
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
