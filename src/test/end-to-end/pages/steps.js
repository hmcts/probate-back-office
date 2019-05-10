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
        enterCaveatPage5: steps.createCaveat.page5,
        emailCaveator: steps.emailNotifications.caveat.emailCaveator,
        reopenCaveat: steps.reopenningCases.caveat.reopenCaveat,
        enterGrantOfProbatePage1: steps.createGrantOfProbate.page1,
        enterGrantOfProbatePage2: steps.createGrantOfProbate.page2,
        enterGrantOfProbatePage3: steps.createGrantOfProbate.page3,
        enterGrantOfProbatePage4: steps.createGrantOfProbate.page4,
        enterGrantOfProbatePage5: steps.createGrantOfProbate.page5,
        enterGrantOfProbatePage6: steps.createGrantOfProbate.page6,
        enterGrantOfProbatePage7: steps.createGrantOfProbate.page7,
        enterGrantOfProbatePage8: steps.createGrantOfProbate.page8,
        enterGrantOfProbatePage9: steps.createGrantOfProbate.page9,
        checkMyAnswers: steps.checkYourAnswers.checkYourAnswers,
        seeCaseDetails: steps.caseDetails.caseDetails,
        chooseNextStep: steps.nextStep.nextStep,
        printCase: steps.printCase.printCase,
        enterEventSummary: steps.eventSummary.eventSummary,
        uploadDocument: steps.documentUpload.documentUpload,
        enterComment: steps.eventSummary.eventSummary,
        markForExamination: steps.markForExamination.markForExamination,
        markForIssue: steps.markForIssue.markForIssue,
        issueGrant: steps.issueGrant.issueGrant,
        selectCaseMatchesForWillLodgement: steps.caseMatches.willLodgement.caseMatches,
        selectCaseMatchesForGrantOfProbate: steps.caseMatches.grantOfProbate.caseMatches,
        selectCaseMatchesForCaveat: steps.caseMatches.caveat.caseMatches,
        enterCaseMatchesComment: steps.eventSummary.eventSummary,
        selectWithdrawalReason: steps.withdrawal.withdrawal,
        enterWithdrawalSummary: steps.eventSummary.eventSummary,
        filterCase: steps.filterCase.filterCase,
        selectCase: steps.selectCase.selectCase,
        legacyCaseSearch: steps.search.legacyCaseSearch,
        legacyCaseSearch2: steps.search.legacyCaseSearch2,
        legacyCaseSearch3: steps.search.legacyCaseSearch3,
        caseSearch: steps.search.caseSearch,
        openCaveatCase: steps.openCase.openCaveatCase
    });
};
