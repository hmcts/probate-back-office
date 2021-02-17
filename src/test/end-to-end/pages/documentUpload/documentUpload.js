'use strict';

const assert = require('assert');

const testConfig = require('src/test/config.js');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = async function (caseType, caseRef, documentUploadConfig) {

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

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '0');
    let optText = '';

    if (caseType === 'gop') {
        optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(2)`});
        assert.equal('Will', optText);
    }

    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(2)`});
    assert.equal(caseType === 'gop' ? 'Will' : 'Email', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '2');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(3)`});
    assert.equal(caseType === 'gop' ? 'Email' : 'Correspondence', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '3');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(4)`});
    assert.equal(caseType === 'gop' ? 'Correspondence' : 'Codicil', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '4');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(5)`});
    assert.equal(caseType === 'gop' ? 'Codicil' : 'Death Certificate', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '5');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(6)`});
    assert.equal(caseType === 'gop' ? 'Death Certificate' : 'Other', optText);

    if (caseType === 'gop') {
        await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '6');
        optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(7)`});
        assert.equal('Other', optText);
    }

    await I.waitForVisible({css: `${documentUploadConfig.id}_1_DocumentLink`});
    await I.attachFile(`${documentUploadConfig.id}_1_DocumentLink`, documentUploadConfig.fileToUploadUrl);

    await I.waitForValue({css: `${documentUploadConfig.id}_1_Comment`}, documentUploadConfig.comment);

    await I.waitForEnabled(commonConfig.continueButton);
    await I.waitForNavigationToComplete(commonConfig.continueButton);
};
