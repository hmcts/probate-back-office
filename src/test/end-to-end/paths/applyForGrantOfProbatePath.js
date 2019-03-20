'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/applyForGrantOfProbate/createCaseConfig');

const applyForGrantOfProbateConfig = require('src/test/end-to-end/pages/applyForGrantOfProbate/applyForGrantOfProbateConfig');
const checkYourAnswersConfig = require('src/test/end-to-end/pages/checkYourAnswers/checkYourAnswersConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/documentUploadConfig');
const documentUploadSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/documentUploadSummaryConfig');
const commentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/commentSummaryConfig');
const generateDepositReceiptSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/generateDepositReceiptSummaryConfig');
const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/caseMatchesConfig');
const caseMatchesCommentSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/caseMatchesCommentSummaryConfig');
const withdrawalSummaryConfig = require('src/test/end-to-end/pages/eventSummary/applyForGrantOfProbate/withdrawalSummaryConfig');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/historyTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/caseDetailsTabConfig');
const testatorTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/testatorTabConfig');
const executorTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/executorTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/documentUploadTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/caseDetailsUpdateTabConfig');
const testatorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/testatorUpdateTabConfig');
const executorUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/executorUpdateTabConfig');
const generateDepositReceiptTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/generateDepositReceiptTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/applyForGrantOfProbate/caseMatchesTabConfig');

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
    I.seeCaseDetails(caseRef, caseDetailsTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, testatorTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, executorTabConfig, applyForGrantOfProbateConfig);

    I.chooseNextStep('Upload document');
    I.uploadDocument(caseRef);
    I.enterEventSummary(caseRef, documentUploadSummaryConfig);
    I.seeCaseDetails(caseRef, documentUploadTabConfig, documentUploadConfig);

    I.chooseNextStep('Amend will lodgement');
    I.enterWillLodgementPage1('update');
    I.enterWillLodgementPage2('update');
    I.enterWillLodgementPage3('update');
    I.checkMyAnswers();
    I.seeCaseDetails(caseRef, caseDetailsUpdateTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, testatorUpdateTabConfig, applyForGrantOfProbateConfig);
    I.seeCaseDetails(caseRef, executorUpdateTabConfig, applyForGrantOfProbateConfig);

    I.chooseNextStep('Add comment');
    I.enterComment(caseRef, commentSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, commentSummaryConfig);

    I.chooseNextStep('Generate deposit receipt');
    I.generateDepositReceipt(caseRef, generateDepositReceiptSummaryConfig);
    I.seeCaseDetails(caseRef, generateDepositReceiptTabConfig, generateDepositReceiptSummaryConfig);

    I.chooseNextStep('Match application');
    I.selectCaseMatches(caseRef, caseMatchesConfig);
    I.enterCaseMatchesComment(caseRef, caseMatchesCommentSummaryConfig);
    I.seeCaseDetails(caseRef, caseMatchesTabConfig, caseMatchesConfig);

    I.chooseNextStep('Withdraw will');
    I.selectWithdrawalReason(caseRef);
    I.enterWithdrawalSummary(caseRef, withdrawalSummaryConfig);
    I.seeCaseDetails(caseRef, historyTabConfig, withdrawalSummaryConfig);

}).retry(testConfig.TestRetryScenarios);
