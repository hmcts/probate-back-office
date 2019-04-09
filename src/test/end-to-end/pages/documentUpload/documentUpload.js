'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function (caseRef, documentUploadConfig, uploadNumber=1) {

    const I = this;
    I.waitForText(documentUploadConfig.waitForText, testConfig.TestTimeToWaitForText);

    I.see(caseRef);

    I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    I.selectOption(`${documentUploadConfig.id}_${uploadNumber-1}_DocumentType`, documentUploadConfig.documentType);
    I.attachFile(`${documentUploadConfig.id}_${uploadNumber-1}_DocumentLink`, documentUploadConfig.fileToUploadUrl);
    I.fillField(`${documentUploadConfig.id}_${uploadNumber-1}_Comment`, documentUploadConfig.comment);

    I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    I.selectOption(`${documentUploadConfig.id}_${uploadNumber}_DocumentType`, documentUploadConfig.documentType);
    I.attachFile(`${documentUploadConfig.id}_${uploadNumber}_DocumentLink`, documentUploadConfig.fileToUploadUrl);
    I.fillField(`${documentUploadConfig.id}_${uploadNumber}_Comment`, documentUploadConfig.comment);
    I.waitForNavigationToComplete(commonConfig.continueButton);

};
