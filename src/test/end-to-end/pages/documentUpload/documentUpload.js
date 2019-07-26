'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, documentUploadConfig) {

    const I = this;
    I.waitForText(documentUploadConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    I.selectOption(`${documentUploadConfig.id}_0_DocumentType`, documentUploadConfig.documentType);
    I.attachFile(`${documentUploadConfig.id}_0_DocumentLink`, documentUploadConfig.fileToUploadUrl);
    I.fillField(`${documentUploadConfig.id}_0_Comment`, documentUploadConfig.comment);

    I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, documentUploadConfig.documentType);
    I.attachFile(`${documentUploadConfig.id}_1_DocumentLink`, documentUploadConfig.fileToUploadUrl);
    I.fillField(`${documentUploadConfig.id}_1_Comment`, documentUploadConfig.comment);

    I.waitForNavigationToComplete(commonConfig.continueButton);

};
