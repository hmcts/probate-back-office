'use strict';

const testConfig = require('src/test/config');
const caseDetailsConfig = require('./caseDetailsConfig');
const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const commentConfig = require('src/test/end-to-end/pages/comment/commentConfig');
const generateDepositReceiptConfig = require('src/test/end-to-end/pages/generateDepositReceipt/generateDepositReceiptConfig');

module.exports = function (caseRef, group) {

    const I = this;

    let counter = 0;

    I.waitForText(caseDetailsConfig.waitForText, testConfig.TestTimeToWaitForText);
    I.see(caseRef);

    caseDetailsConfig.tabsList.forEach(function (value) {

        if (group === value.group) {
            I.click(value.tabName);

            value.fields.forEach(function (fieldName) {
                I.see(fieldName);
            });

            if (counter === 0 && value.group === '1') {
                value.dataKeys.forEach(function (dataKey) {
                    I.see(checkYourAnswersConfig[dataKey]);
                });
            }

            if (counter > 0 && value.group === '1') {
                value.dataKeys.forEach(function (dataKey) {
                    I.see(createWillLodgementConfig[dataKey]);
                });
            }

            if (value.group === '2') {
                I.say('2', 'red');
                value.dataKeys.forEach(function (dataKey) {
                    I.see(documentUploadConfig[dataKey]);
                });
            }

            if (value.group === '3') {
                I.say('3', 'red');
                value.dataKeys.forEach(function (dataKey) {
                    I.see(createWillLodgementConfig[dataKey]);
                });
            }

            if (value.group === '4') {
                I.say('4', 'red');
                value.dataKeys.forEach(function (dataKey) {
                    I.see(commentConfig[dataKey]);
                });
            }

            if (value.group === '5') {
                I.say('5', 'red');
                value.dataKeys.forEach(function (dataKey) {
                    I.see(generateDepositReceiptConfig[dataKey]);
                });
            }
            counter += 1;
        }
    });

    I.selectOption('#next-step', caseDetailsConfig[`step${group}`]);
    I.waitForNavigationToComplete(caseDetailsConfig.goButton);
};
