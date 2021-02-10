'use strict';

const testConfig = require('src/test/config.js');
const assert = require('assert');

// pre-condition 1 - caseDetails.js already invoked to switch to this tab
// pre-condition 2 - we haven't done something daft like store the data label
//                   as a value for one of the other fields on the tab
module.exports = async function (tabName, fieldLabelsNotToBeShown) {

    const I = this;

    let numElements;

    for (let i = 0; i < fieldLabelsNotToBeShown.length; i++) {
        // eslint-disable-next-line
        numElements = await I.grabNumberOfVisibleElements({ xpath: `//div[contains(@class, 'case-viewer-label')][text()='${fieldLabelsNotToBeShown[i]}']`});
        assert (numElements === 0);
    }
};
