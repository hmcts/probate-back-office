'use strict';

const assert = require('assert');

// pre-condition - caseDetails.js already invoked to switch to this tab
module.exports = async function (tabName, fieldLabelsNotToBeShown) {

    const I = this;

    let numElements;

    for (let i = 0; i < fieldLabelsNotToBeShown.length; i++) {
        // eslint-disable-next-line
        numElements = await I.grabNumberOfVisibleElements({ xpath: `//div[contains(@class, 'case-viewer-label')][text()='${fieldLabelsNotToBeShown[i]}']`});
        assert (numElements === 0);
    }
};
