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
        enterCaveatPage1: steps.createCaveat.page1,
        enterCaveatPage2: steps.createCaveat.page2,
        enterCaveatPage3: steps.createCaveat.page3,
        enterCaveatPage4: steps.createCaveat.page4,
        emailCaveator: steps.emailNotifications.caveat.emailCaveator,
        checkMyAnswers: steps.checkYourAnswers.checkYourAnswers,
        seeCaseDetails: steps.caseDetails.caseDetails,
        chooseNextStep: steps.nextStep.nextStep,
        enterEventSummary: steps.eventSummary.eventSummary,
        uploadDocument: steps.documentUpload.documentUpload,
        enterComment: steps.eventSummary.eventSummary,
        selectCaseMatchesForWillLodgement: steps.caseMatches.willLodgement.caseMatches,
        selectCaseMatchesForCaveat: steps.caseMatches.caveat.caseMatches,
        selectWithdrawalReason: steps.withdrawal.withdrawal
    });
};
