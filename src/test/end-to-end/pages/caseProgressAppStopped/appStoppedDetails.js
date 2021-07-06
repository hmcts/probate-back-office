'use strict';
const caseProgressConfig = require('./caseProgressConfig');

module.exports = async function () {
    const I = this;
    await I.waitForText(caseProgressConfig.AppStoppedHeader);
    await I.waitForText(caseProgressConfig.AppStoppedReasonText);
    await I.waitForText(caseProgressConfig.AppStoppedAdditionalText);
    await I.waitForNavigationToComplete('button[type="submit"]:enabled', true);
};
