'use strict';

const testConfig = require('src/test/config.js');
const documentUploadConfig = require('./documentUploadConfig.json');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef) {

    const I = this;
    I.waitForText(documentUploadConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click(documentUploadConfig.addNewButton);

    I.selectOption('#documentsUploaded_0_DocumentType', documentUploadConfig.documentType);
    I.attachFile('#documentsUploaded_0_DocumentLink', documentUploadConfig.fileToUploadUrl);
    I.fillField('#documentsUploaded_0_Comment', documentUploadConfig.comment);

    I.click(documentUploadConfig.addNewButton);

    I.selectOption('#documentsUploaded_1_DocumentType', documentUploadConfig.documentType);
    I.attachFile('#documentsUploaded_1_DocumentLink', documentUploadConfig.fileToUploadUrl);
    I.fillField('#documentsUploaded_1_Comment', documentUploadConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
