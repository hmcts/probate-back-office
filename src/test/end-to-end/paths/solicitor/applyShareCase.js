'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

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
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');

const caseDetailsUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsUpdateTabConfig');
const deceasedUpdateTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedUpdateTabConfig');


Feature('Solicitor - Share A Case').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Share A Case';


Scenario(scenarioName, async function ({I}) {
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';
    const SAC = true;

    await I.logInfo(scenarioName, 'Login as Solicitor');
    await I.authenticateWithIdamIfAvailable(SAC);


  //  let nextStepName = 'Deceased details';
  //  let endState = 'Application created';
  //  await I.logInfo(scenarioName, nextStepName);
  //  await I.selectNewCase();
  //  await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
   // await I.applyForProbatePage1();
  //  await I.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
  //  await I.cyaPage();

  //  await I.seeEndState(endState);

   // const caseRef = await I.getCaseRefFromUrl();

   // await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
   // await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, applyProbateConfig);
    await I.shareCaseSelection();

}).retry(testConfig.TestRetryScenarios);
