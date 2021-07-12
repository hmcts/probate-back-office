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
        await I.wait(0.25); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_0_Comment`});
    await I.fillField({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25); // needed in order to be able to switch off auto delay for local dev
    }
    await I.waitForValue({css: `${documentUploadConfig.id}_0_Comment`}, documentUploadConfig.comment);
    await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentType`});
    await I.selectOption({css: `${documentUploadConfig.id}_0_DocumentType`}, documentUploadConfig.documentType);
    await I.waitForVisible({css: `${documentUploadConfig.id}_0_DocumentLink`});
    await I.attachFile({css: `${documentUploadConfig.id}_0_DocumentLink`}, documentUploadConfig.fileToUploadUrl);

    await I.wait(5);

    /* add the following properties to documentUploadConfig.json once caseworker moves
       from CCD UI to ExUI.
       CCD UI currently seems to put options in ddl in a random order - differs from local setup to pipeline.

       caveat:  "docTypes": ["Email", "Correspondence", "Codicil", "Death Certificate", "Warning", "Other"]
       grantOfProbate: "docTypes": ["Will", "Email", "Correspondence", "Codicil", "Death Certificate", "Other"]
       willLodgement: "docTypes": ["Email", "Correspondence", "Codicil", "Death Certificate", "Other"]
    */

    if (documentUploadConfig.docTypes) {
        for (let i = 0; i < documentUploadConfig.docTypes.length; i++) {
            // eslint-disable-next-line no-await-in-loop
            const optText = await I.grabTextFrom({css: `${documentUploadConfig.id}_0_DocumentType option:nth-child(${i})`});
            if (optText !== documentUploadConfig.docTypes[i]) {
                console.info('document upload doc types not as expected.');
                console.info(`expected: ${documentUploadConfig.docTypes[i]}, actual: ${optText}`);
                console.info('doctype select html:');
                // eslint-disable-next-line no-await-in-loop
                console.info(await I.grabHTMLFrom({css: `${documentUploadConfig.id}_0_DocumentType`}));
            }
            assert(optText === documentUploadConfig.docTypes[i]);
        }
    }
    
    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
    await I.wait(5);
};
