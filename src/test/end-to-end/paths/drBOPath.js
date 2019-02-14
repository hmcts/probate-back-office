'use strict';

//const taskListContent = require('app/resources/en/translation/tasklist');
const TestConfigurator = new (require('src/test/end-to-end/helpers/TestConfigurator'))();
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig.json');

const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const createWillLodgementConfig = require('src/test/end-to-end/pages/createWillLodgement/createWillLodgementConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const uploadDocumentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/uploadDocumentSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/commentSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/historyTabConfig');
const generalDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/generalDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/executorTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/documentUploadTabConfig');
const generalDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/generalDetailsUpdateTabConfig');
const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/testatorUpdateTabConfig');
const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/executorUpdateTabConfig');

Feature('Back Office - Will Lodgement for a Personal Applicant').retry(TestConfigurator.getRetryFeatures());

// eslint complains that the Before/After are not used but they are by codeceptjs
// so we have to tell eslint to not validate these
// eslint-disable-next-line no-undef
/*
Before(() => {
    TestConfigurator.getBefore();
});
*/

// eslint-disable-next-line no-undef
/*
After(() => {
    TestConfigurator.getAfter();
});
*/


Scenario(TestConfigurator.idamInUseText('Multiple Executors'), async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text, createCaseConfig.list3_text);
    I.enterWillLodgementPage1('create');
    I.enterWillLodgementPage2('create');
    I.enterWillLodgementPage3('create');
    I.checkMyAnswers();

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop().match(/.{4}/g).join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig);
    I.seeCaseDetails(caseRef, generalDetailsTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorTabConfig, createWillLodgementConfig);

    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, uploadDocumentSummaryConfig);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();

    I.seeCaseDetails(caseRef, generalDetailsUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, createWillLodgementConfig);
    I.seeCaseDetails(caseRef, executorUpdateTabConfig, createWillLodgementConfig);

    I.enterEventSummary(caseRef, commentSummaryConfig);

    let nextStep = 'Generate deposit receipt';
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig, nextStep);

    // I.generateDepositReceipt(caseRef, '5');
    // I.seeCaseDetails(caseRef, '5');
    //
    // I.selectMatchedCases(caseRef);
    // I.enterMatchedCasesComment(caseRef);
    // I.seeCaseDetails(caseRef, '6');

});
