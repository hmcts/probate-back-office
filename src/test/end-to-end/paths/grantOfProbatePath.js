'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const printCaseSummaryConfig = require('src/test/end-to-end/pages/eventSummary/grantOfProbate/printCaseSummaryConfig');
const addCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/grantOfProbate/addCommentSummaryConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/grantOfProbate/documentUploadSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/grantOfProbate/withdrawalSummaryConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsUpdateTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/paymentDetailsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const handleEvidenceSummaryConfig = require('src/test/end-to-end/pages/eventSummary/grantOfProbate/handleEvidenceSummaryConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Grant of Probate for a Personal Applicant', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);
    I.enterGrantOfProbatePage1('create');
    I.enterGrantOfProbatePage2('create');
    I.enterGrantOfProbatePage3('create');
    I.enterGrantOfProbatePage4('create');
    I.enterGrantOfProbatePage5('create');
    I.enterGrantOfProbatePage6('create');
    I.enterGrantOfProbatePage7('create');
    I.enterGrantOfProbatePage8('create');
    I.enterGrantOfProbatePage9('create');
    I.checkMyAnswers();

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig);
    I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, paymentDetailsTabConfig, createGrantOfProbateConfig);

    I.chooseNextStep('Handle supplementary evidence');
    I.handleEvidence(caseRef);
    I.enterEventSummary(caseRef, handleEvidenceSummaryConfig);

    I.chooseNextStep('Amend case details');
    I.enterGrantOfProbatePage1('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage2('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage3('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage4('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage5('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage6('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage7('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.entergrantOfProbatePage8('update');
    I.checkMyAnswers();
    // I.chooseNextStep('Amend case details');
    // I.entergrantOfProbatePage9('update');
    // I.checkMyAnswers();
    pause();

    I.seeCaseDetails(caseRef, deceasedUpdateTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, createGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, createGrantOfProbateConfig);

    I.chooseNextStep('Print the case');
    I.printCase(caseRef);
    I.enterEventSummary(caseRef, printCaseSummaryConfig);

    I.chooseNextStep('Add Comment');
    I.enterComment(caseRef, addCommentSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, addCommentSummaryConfig);

    I.chooseNextStep('Upload Documents');
    I.uploadDocument(caseRef, documentUploadConfig);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    I.chooseNextStep('Withdraw application');
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

}).retry(testConfig.TestRetryScenarios);
