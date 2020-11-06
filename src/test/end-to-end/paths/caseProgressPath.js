'use strict';

const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');
const caseProgressConfig = require('src/test/end-to-end/pages/caseProgress/caseProgressConfig');


const caseMatchesConfig = require('src/test/end-to-end/pages/caseMatches/grantOfProbate/caseMatchesConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const documentUploadConfig = require('src/test/end-to-end/pages/documentUpload/grantOfProbate/documentUploadConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const issueGrantConfig = require('src/test/end-to-end/pages/issueGrant/issueGrantConfig');
const markForExaminationConfig = require('src/test/end-to-end/pages/markForExamination/markForExaminationConfig');
const markForIssueConfig = require('src/test/end-to-end/pages/markForIssue/markForIssueConfig');

const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
const caseMatchesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseMatchesTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const docNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/docNotificationsTabConfig');
const documentUploadTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/documentUploadTabConfig');
const examChecklistTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/examChecklistTabConfig');
const grantNotificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/grantNotificationsTabConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/paymentDetailsTabConfig');

const applicantDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsUpdateTabConfig');
const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');


Feature('Back Office').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Case Progress E2E', async function (I) {
    // IDAM
    try {
        await I.authenticateWithIdamIfAvailable(true);
        await I.selectNewCase();
        await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor, 0);
        await I.waitForNavigationToComplete(commonConfig.continueButton);
        await I.enterCaseProgressPage1();
        console.info('Create new case clicked');

    } catch (e) {
        console.error(`case progress error:${e.message}\nStack:${e.stack}`);
        return Promise.reject(e);
    }

}).retry(0); //testConfig.TestRetryScenarios);