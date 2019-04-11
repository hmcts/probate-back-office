'use strict';

const testConfig = require('src/test/config');
const filterCaseConfig = require('src/test/end-to-end/pages/filterCase/filterCaseConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Legacy search', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();
    I.filterCase(filterCaseConfig.list1_text, filterCaseConfig.list2_text, filterCaseConfig.list3_text);
    I.selectCase();
    // TODO: Uncomment the following once we have a solution for data setup
//    I.legacyCaseSearch();
//    I.legacyCaseSearch2();
//    I.legacyCaseSearch3();
}).retry(testConfig.TestRetryScenarios);
