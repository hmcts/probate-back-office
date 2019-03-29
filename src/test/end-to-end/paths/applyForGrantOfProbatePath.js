'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/applyForGrantOfProbate/createCaseConfig');
const applyForGrantOfProbateConfig = require('src/test/end-to-end/pages/applyForGrantOfProbate/applyForGrantOfProbateConfig');
const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const printCaseSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/printCaseSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/commentSummaryConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/applyForGrantOfProbate/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/documentUploadSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/withdrawalSummaryConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/applicantDetailsTabConfig');
const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/applicantDetailsUpdateTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/deceasedTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/deceasedUpdateTabConfig');
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/paymentDetailsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/historyTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/caseDetailsTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/caseDetailsUpdateTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/documentUploadTabConfig');
const handleEvidenceSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/handleEvidenceSummaryConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('Grant of Probate for a Personal Applicant', async function (I) {

    // IdAM
    I.authenticateWithIdamIfAvailable();

    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text, createCaseConfig.list3_text);
    I.enterApplyForGrantOfProbatePage1('create');
    I.enterApplyForGrantOfProbatePage2('create');
    I.enterApplyForGrantOfProbatePage3('create');
    I.enterApplyForGrantOfProbatePage4('create');
    I.enterApplyForGrantOfProbatePage5('create');
    I.enterApplyForGrantOfProbatePage6('create');
    I.enterApplyForGrantOfProbatePage7('create');
    I.enterApplyForGrantOfProbatePage8('create');
    I.enterApplyForGrantOfProbatePage9('create');
    I.checkMyAnswers();

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    I.seeCaseDetails(caseRef, historyTabConfig, checkYourAnswersConfig);
    I.seeCaseDetails(caseRef, deceasedTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, paymentDetailsTabConfig, applyForGrantOfProbateConfig);

    I.chooseNextStep('Handle supplementary evidence');
    I.handleEvidence(caseRef);
    I.enterEventSummary(caseRef, handleEvidenceSummaryConfig);

    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage1('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage2('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage3('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage4('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage5('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage6('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage7('update');
    I.checkMyAnswers();
    I.chooseNextStep('Amend case details');
    I.enterApplyForGrantOfProbatePage8('update');
    I.checkMyAnswers();
    // I.chooseNextStep('Amend case details');
    // I.enterApplyForGrantOfProbatePage9('update');
    // I.checkMyAnswers();

    I.seeCaseDetails(caseRef, deceasedUpdateTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, applicantDetailsUpdateTabConfig, applyForGrantOfProbateConfig);

    I.chooseNextStep('Print the case');
    I.printCase(caseRef);
    I.enterEventSummary(caseRef, printCaseSummaryConfig);

    I.chooseNextStep('Add Comment');
    I.enterComment(caseRef, commentSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig);

    I.chooseNextStep('Upload Documents');
    I.uploadDocument(caseRef, documentUploadConfig);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    I.chooseNextStep('Withdraw application');
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

}).retry(testConfig.TestRetryScenarios);
