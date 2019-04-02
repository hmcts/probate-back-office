'use strict';

const requireDirectory = require('require-directory');
const steps = requireDirectory(module);

module.exports = function () {
    return actor({

        // Login
        authenticateWithIdamIfAvailable: steps.IDAM.signIn,
        selectNewCase: steps.newCase.newCase,
        selectCaseTypeOptions: steps.createCase.createCase,
        handleEvidence: steps.handleEvidence.handleEvidence,
        enterWillLodgementPage1: steps.createWillLodgement.page1,
        enterWillLodgementPage2: steps.createWillLodgement.page2,
        enterWillLodgementPage3: steps.createWillLodgement.page3,
        enterCaveatPage1: steps.createCaveat.page1,
        enterCaveatPage2: steps.createCaveat.page2,
        enterCaveatPage3: steps.createCaveat.page3,
        enterCaveatPage4: steps.createCaveat.page4,
        emailCaveator: steps.emailNotifications.caveat.emailCaveator,
        reopenCaveat: steps.reopenningCases.caveat.reopenCaveat,
        enterApplyForGrantOfProbatePage1: steps.applyForGrantOfProbate.page1,
        enterApplyForGrantOfProbatePage2: steps.applyForGrantOfProbate.page2,
        enterApplyForGrantOfProbatePage3: steps.applyForGrantOfProbate.page3,
        enterApplyForGrantOfProbatePage4: steps.applyForGrantOfProbate.page4,
        enterApplyForGrantOfProbatePage5: steps.applyForGrantOfProbate.page5,
        enterApplyForGrantOfProbatePage6: steps.applyForGrantOfProbate.page6,
        enterApplyForGrantOfProbatePage7: steps.applyForGrantOfProbate.page7,
        enterApplyForGrantOfProbatePage8: steps.applyForGrantOfProbate.page8,
        enterApplyForGrantOfProbatePage9: steps.applyForGrantOfProbate.page9,
        checkMyAnswers: steps.checkYourAnswers.checkYourAnswers,
        seeCaseDetails: steps.caseDetails.caseDetails,
        chooseNextStep: steps.nextStep.nextStep,
        printCase: steps.printCase.printCase,
        enterEventSummary: steps.eventSummary.eventSummary,
        uploadDocument: steps.documentUpload.documentUpload,
        enterComment: steps.eventSummary.eventSummary,
        markAsReadyForExamination: steps.markAsReady.markAsReady,
        selectCaseMatchesForWillLodgement: steps.caseMatches.willLodgement.caseMatches,
        selectCaseMatchesForGrantOfProbate: steps.caseMatches.grantOfProbate.caseMatches,
        selectCaseMatchesForCaveat: steps.caseMatches.caveat.caseMatches,
        selectWithdrawalReason: steps.withdrawal.withdrawal
    });
};
