'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (nextStep) {

    const I = this;

    I.selectOption('#next-step', nextStep);
    I.wait(1);
    I.waitForNavigationToComplete(commonConfig.goButton);

};
