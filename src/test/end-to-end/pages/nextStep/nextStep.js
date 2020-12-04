'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (nextStep) {

    const I = this;

    await I.selectOption('#next-step', nextStep);
    await I.waitForNavigationToComplete(commonConfig.goButton);
};
