'use strict';
const assert = require('assert');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

// NO LONGER SEEMS TO BE PART OF THE FLOW
// grant of probate details part 7 - confirmation
module.exports = async function () {
    const I = this;
    await I.waitForText('Confirm your client has agreed with the legal statement and declaration&nbsp;-&nbsp;');
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
