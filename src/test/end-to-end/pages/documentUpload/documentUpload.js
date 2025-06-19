'use strict';

const assert = require('assert');

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, documentUploadConfig) {

    const I = this;
    await I.waitForText(documentUploadConfig.waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    await I.waitForEnabled({css: `${documentUploadConfig.id}>div`});
    await I.click({css: `${documentUploadConfig.id} > div > button`});
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_0_Comment`});
    await I.wait(2);
    await I.fillField({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    await I.wait(1);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    const docLink = {css: `${documentUploadConfig.id}_0_DocumentLink`};
    await I.waitForValue({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentType`});
    await I.selectOption({css: `${documentUploadConfig.id}_0_DocumentType`}, documentUploadConfig.documentType[0]);
    await I.scrollTo(docLink);
    await I.waitForVisible(docLink);
    await I.waitForEnabled(docLink);
    await I.attachFile(docLink, documentUploadConfig.fileToUploadUrl);
    await I.wait(testConfig.DocumentUploadDelay);

    if (documentUploadConfig.documentType) {
        for (let i = 0; i < documentUploadConfig.documentType.length; i++) {
            // eslint-disable-next-line no-await-in-loop
            const optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_0_DocumentType option:nth-child(${i+2})`});
            if (optText !== documentUploadConfig.documentType[i]) {
                console.info('document upload doc types not as expected.');
                console.info(`expected: ${documentUploadConfig.documentType[i]}, actual: ${optText}`);
                console.info('doctype select html:');
                // eslint-disable-next-line no-await-in-loop
                console.info(await I.grabHTMLFrom ({css: `${documentUploadConfig.id}_0_DocumentType`}));
            }
            console.info('Document upload type number ' + (i+1) + ' in list - ' + documentUploadConfig.documentType[i]);
            assert(optText === documentUploadConfig.documentType[i]);
        }
    }

    //await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentLink`});
    await I.attachFile(`${documentUploadConfig.id}_0_DocumentLink`, documentUploadConfig.fileToUploadUrl);

    await I.waitForValue({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);

    // small delay to allow hidden vars to be set
    await I.wait(testConfig.DocumentUploadDelay);
    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
