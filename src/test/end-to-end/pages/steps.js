'use strict';

const requireDirectory = require('require-directory');
const steps = requireDirectory(module);

module.exports = function () {
    return actor({

        // Login
        authenticateWithIdamIfAvailable: steps.IDAM.signIn,
        selectNewCase: steps.newCase.newCase,
        selectCaseTypeOptions: steps.createCase.createCase,
        enterWillLodgementPage1: steps.createWillLodgement.page1,
        enterWillLodgementPage2: steps.createWillLodgement.page2,
        enterWillLodgementPage3: steps.createWillLodgement.page3,
        checkMyAnswers: steps.checkYourAnswers.checkYourAnswers,
        seeCaseDetails: steps.caseDetails.caseDetails,
        uploadDocument: steps.documentUpload.documentUpload,
        enterEventSummary: steps.eventSummary.eventSummary,
        enterComment: steps.eventSummary.eventSummary,
        generateDepositReceipt: steps.eventSummary.eventSummary,
        selectCaseMatches: steps.caseMatches.caseMatches,
        enterCaseMatchesComment: steps.eventSummary.eventSummary,
        selectWithdrawalReason: steps.withdrawal.withdrawal,
        enterWithdrawalSummary: steps.eventSummary.eventSummary
    });
};
