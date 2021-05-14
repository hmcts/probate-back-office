'use strict';
const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function () {
    const I = this;
    assert (headingHtml.includes ('Complete application'));
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
