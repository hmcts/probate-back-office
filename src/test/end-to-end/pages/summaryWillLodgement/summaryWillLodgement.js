'use strict';

const testConfig = require('src/test/config.js');
const summaryWillLodgementConfig = require('./summaryWillLodgementConfig.json');

module.exports = async function () {

        const I = this;
        I.waitForText(summaryWillLodgementConfig.waitForText, testConfig.TestTimeToWaitForText);

        let url = await I.grabCurrentUrl();
        const caseRef =  url.split('/').pop().match(/.{4}/g).join('-');
        console('caseRef>>>', caseRef);
        pause();
        I.waitForNavigationToComplete(checkYourAnswersConfig.locator);
};
