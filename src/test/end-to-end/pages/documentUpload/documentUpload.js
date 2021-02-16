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

    await I.click({type: 'button'}, `${documentUploadConfig.id}>div`);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25); // needed in order to be able to switch off auto delay for local dev
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_1_Comment`});
    await I.fillField({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);
    if (!testConfig.TestAutoDelayEnabled) {
        await I.wait(0.25); // needed in order to be able to switch off auto delay for local dev
    }
    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentType`});
    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, documentUploadConfig.documentType);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '1');
    let optText = '';
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(2)`});
    assert.equal('Will', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '2');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(3)`});
    assert.equal('Email', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '3');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(4)`});
    assert.equal('Correspondence', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '4');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(5)`});
    assert.equal('Codicil', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '5');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(6)`});
    assert.equal('Death Certificate', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '6');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(7)`});
    assert.equal('Other', optText);

    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentLink`});
    await I.attachFile(`${documentUploadConfig.id}_1_DocumentLink`, documentUploadConfig.fileToUploadUrl);

    await I.waitForValue({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
