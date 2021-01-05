'use strict';

const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const applicationDetailsConfig = require('./applicationDetails');

module.exports = async function () {
    const I = this;
    await I.waitForElement('#caveatorForenames');
    await I.fillField('#caveatorForenames', applicationDetailsConfig.page1_caveator_forename);
    await I.fillField('#caveatorSurname', applicationDetailsConfig.page1_caveator_surname);

    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
