'use strict';

const assert = require('assert');

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseRef, documentUploadConfig, uploadDocNum) {

    const I = this;
    const uploadDocItem = (documentUploadConfig.uploadDocList);
    await I.waitForText(uploadDocItem[uploadDocNum].waitForText, testConfig.WaitForTextTimeout);

    await I.see(caseRef);

    await I.waitForEnabled({css: `${uploadDocItem[uploadDocNum].id}>div`});
    await I.click({type: 'button'}, `${uploadDocItem[uploadDocNum].id}>div`);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_Comment`});
    await I.fillField({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_Comment`}, uploadDocItem[uploadDocNum].comment);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(testConfig.ManualDelayShort); // needed in order to be able to switch off auto delay for local dev
    }

    const docLink = {css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentLink`};

    await I.waitForValue({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_Comment`}, uploadDocItem[uploadDocNum].comment);
    await I.waitForVisible({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentType`});
    await I.selectOption({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentType`}, uploadDocItem[uploadDocNum].documentType);
    await I.scrollTo(docLink);
    await I.waitForVisible(docLink);
    await I.waitForEnabled(docLink);
    await I.attachFile(docLink, uploadDocItem[uploadDocNum].fileToUploadUrl);
    await I.wait(testConfig.DocumentUploadDelay);

    /* add the following properties to documentUploadConfig.json once caseworker moves
       from CCD UI to ExUI.
       CCD UI currently seems to put options in ddl in a random order - differs from local setup to pipeline.

       caveat:  "docTypes": ["Email", "Correspondence", "Codicil", "Death Certificate", "Warning", "Other"]
       grantOfProbate: "docTypes": ["Will", "Email", "Correspondence", "Codicil", "Death Certificate", "Other"]
       willLodgement: "docTypes": ["Email", "Correspondence", "Codicil", "Death Certificate", "Other"]
    */

    if (documentUploadConfig.docTypes) {
        for (let i = uploadDocNum; i < documentUploadConfig.docTypes.length; i++) {
            // eslint-disable-next-line no-await-in-loop
            const optText = await I.grabTextFrom ({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentType option:nth-child(${i+2})`});
            if (optText !== documentUploadConfig.docTypes[i]) {
                console.info('document upload doc types not as expected.');
                console.info(`expected: ${documentUploadConfig.docTypes[i]}, actual: ${optText}`);
                console.info('doctype select html:');
                // eslint-disable-next-line no-await-in-loop
                console.info(await I.grabHTMLFrom ({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentType`}));
            }
            assert(optText === documentUploadConfig.docTypes[i]);
        }
    }

    if (uploadDocNum > 0) {
        let reattachDocNum = uploadDocNum;
        while (reattachDocNum !== 0){
            reattachDocNum -= 1;
            await I.waitForVisible({css: `${uploadDocItem[uploadDocNum].id}_${reattachDocNum}_DocumentLink`});
            await I.attachFile(`${uploadDocItem[uploadDocNum].id}_${reattachDocNum}_DocumentLink`, uploadDocItem[reattachDocNum].fileToUploadUrl);

            await I.waitForValue({css: `${uploadDocItem[uploadDocNum].id}_${reattachDocNum}_Comment`}, uploadDocItem[reattachDocNum].comment);
            await I.wait(testConfig.DocumentUploadDelay);
        }
    }

    // This section ensures the flow is not stuck why trying to press submit button
    await I.waitForVisible({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentLink`});
    await I.attachFile(`${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_DocumentLink`, uploadDocItem[uploadDocNum].fileToUploadUrl);
    await I.waitForValue({css: `${uploadDocItem[uploadDocNum].id}_${uploadDocNum}_Comment`}, uploadDocItem[uploadDocNum].comment);

    // small delay to allow hidden vars to be set
    await I.wait(testConfig.DocumentUploadDelay);
    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
