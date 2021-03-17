'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, documentUploadConfig) {

    const I = this;
    await I.waitForText(documentUploadConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    await I.fillField(`${documentUploadConfig.id}_0_Comment`, documentUploadConfig.comment);
    await I.selectOption(`${documentUploadConfig.id}_0_DocumentType`, documentUploadConfig.documentType);
    await I.attachFile(`${documentUploadConfig.id}_0_DocumentLink`, documentUploadConfig.fileToUploadUrl);
    await I.fillField(`${documentUploadConfig.id}_0_Comment`, documentUploadConfig.comment);
    await I.waitForValue({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);

    await I.click({type: 'button'}, `${documentUploadConfig.id}>div`);

    await I.fillField(`${documentUploadConfig.id}_1_Comment`, documentUploadConfig.comment);
    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentType`});
    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, documentUploadConfig.documentType);
    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentLink`});
    await I.attachFile(`${documentUploadConfig.id}_1_DocumentLink`, documentUploadConfig.fileToUploadUrl);

    await I.waitForValue({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
