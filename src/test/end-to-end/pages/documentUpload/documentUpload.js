'use strict';

const assert = require('assert');

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, documentUploadConfig) {

    const I = this;
    await I.waitForText(documentUploadConfig.waitForText, testConfig.TestTimeToWaitForText);

    await I.see(caseRef);

    await I.click({type: 'button'}, `${documentUploadConfig.id}>div`);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_0_Comment`});
    await I.fillField({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }
    await I.waitForValue({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentType`});
    await I.selectOption({css: `${documentUploadConfig.id}_0_DocumentType`}, documentUploadConfig.documentType);
    await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentLink`});
    await I.attachFile({css: `${documentUploadConfig.id}_0_DocumentLink`}, documentUploadConfig.fileToUploadUrl);

    await I.click({type: 'button'}, `${documentUploadConfig.id}>div`);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_1_Comment`});
    await I.fillField({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }
    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentType`});
    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, documentUploadConfig.documentType);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '1');

    if (documentUploadConfig.docTypes) {
        for (let i=0; i < documentUploadConfig.docTypes.length; i++) {
            // eslint-disable-next-line no-await-in-loop
            const optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(${i+2})`});
            assert(optText === documentUploadConfig.docTypes[i]);
        }
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentLink`});
    await I.attachFile(`${documentUploadConfig.id}_1_DocumentLink`, documentUploadConfig.fileToUploadUrl);

    await I.waitForValue({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
