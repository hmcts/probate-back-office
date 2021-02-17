'use strict';

const assert = require('assert');

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

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '1');
    let optText = '';
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(2)`});
    //   assert.equal('Will', optText);
    assert.equal(caseType == 'gor' ? 'Will' : 'Email', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '2');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(3)`});
    assert.equal(caseType == 'gor' ? 'Email' : 'Correspondence', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '3');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(4)`});
    assert.equal(caseType == 'gor' ? 'Correspondence' : 'Codicil', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '4');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(5)`});
    assert.equal(caseType == 'gor' ? 'Codicil' : 'Death Certificate', optText);

    await I.selectOption(`${documentUploadConfig.id}_1_DocumentType`, '5');
    optText = await I.grabTextFrom ({css: `${documentUploadConfig.id}_1_DocumentType option:nth-child(6)`});
    assert.equal(caseType == 'gor' ? 'Death Certificate' : 'Other', optText);

    if (caseType == 'gor') {
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
