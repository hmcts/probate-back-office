'use strict';

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
const createGrantOfProbateConfig = require('src/test/end-to-end/pages/createGrantOfProbate/createGrantOfProbateConfig');
const eventSummaryConfig = require('src/test/end-to-end/pages/eventSummary/eventSummaryConfig');
const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/historyTabConfig');
const deceasedTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/deceasedTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/caseDetailsTabConfig');
const applicantDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/applicantDetailsTabConfig');
const copiesTabConfig = require('src/test/end-to-end/pages/caseDetails/grantOfProbate/copiesTabConfig');

Feature('Back Office').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Grant of Probate - PA1P/PA1A/Solicitors Case Creation';
Scenario(scenarioName, async function ({I}) {
    // BO Grant of Representation (Personal): Case created

    const unique_deceased_user = Date.now();

    await I.logInfo(scenarioName, 'Login as Caseworker');
    await I.authenticateWithIdamIfAvailable(false);

    let nextStepName = 'PA1P/PA1A/Solicitors';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_gor);

    await I.logInfo(scenarioName, 'enterGrantOfProbatePage1');
    await I.enterGrantOfProbatePage1('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage2');
    await I.enterGrantOfProbatePage2('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage3');
    await I.enterGrantOfProbatePage3('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage4');
    await I.enterGrantOfProbatePage4('create', unique_deceased_user);
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage5');
    await I.enterGrantOfProbatePage5('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage6');
    await I.enterGrantOfProbatePage6('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage7');
    await I.enterGrantOfProbatePage7('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage8');
    await I.enterGrantOfProbatePage8('create');
    await I.logInfo(scenarioName, 'enterGrantOfProbatePage9');
    await I.enterGrantOfProbatePage9();

    await I.checkMyAnswers(nextStepName);

    const caseRef = await I.getCaseRefFromUrl();
    const endState = 'Case created';

    await I.logInfo(scenarioName, 'Verify case created', caseRef);
    await I.seeCaseDetails(caseRef, historyTabConfig, eventSummaryConfig, nextStepName, endState);
    await I.seeCaseDetails(caseRef, deceasedTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, createGrantOfProbateConfig);
    await I.dontSeeCaseDetails(caseDetailsTabConfig.fieldsNotPresent);
    await I.seeCaseDetails(caseRef, applicantDetailsTabConfig, createGrantOfProbateConfig);
    await I.seeCaseDetails(caseRef, copiesTabConfig, createGrantOfProbateConfig);

}).retry(testConfig.TestRetryScenarios);
