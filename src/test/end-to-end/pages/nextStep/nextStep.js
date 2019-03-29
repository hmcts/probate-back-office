'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (nextStep) {

    const I = this;

    I.selectOption('#next-step', nextStep);
    pause();
    I.waitForNavigationToComplete(commonConfig.goButton);

};
