'use strict';
const dateFns = require('date-fns');

const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');

const applyCaveatConfig = require('src/test/end-to-end/pages/solicitorApplyCaveat/applyCaveat/applyCaveat');
const applicationDetailsConfig = require('src/test/end-to-end/pages/solicitorApplyCaveat/applicationDetails/applicationDetails');
const completeApplicationConfig = require('src/test/end-to-end/pages/solicitorApplyCaveat/completeApplication/completeApplication');

const historyTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/historyTabConfig');
const caseDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/caseDetailsTabConfig');
const caveatorDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/caveatDetailsTabConfig');
const notificationsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/notificationsTabConfig');
const deceasedDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/deceasedDetailsTabConfig');
const serviceRequestTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

Feature('Solicitor - Apply Caveat').retry(testConfig.TestRetryFeatures);
const scenarioName = 'Solicitor - Apply Caveat';
Scenario(scenarioName, async function ({I}) {

    await I.logInfo(scenarioName, 'Login as Solicitor');
    await I.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Application details';
    let endState = 'Caveat created';
    await I.logInfo(scenarioName, nextStepName);
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.applyCaveatPage1();
    await I.applyCaveatPage2();
    await I.cyaPage();

    await I.seeEndState(endState);

    const caseRef = await I.getCaseRefFromUrl();

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, applyCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, applyCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatDetailsTabConfig, applyCaveatConfig);

    endState = 'Caveat updated';
    await I.logInfo(scenarioName, nextStepName, caseRef);
    await I.chooseNextStep(nextStepName);
    await I.caveatApplicationDetailsPage1();
    await I.caveatApplicationDetailsPage2();
    await I.cyaPage();

    await I.seeEndState(endState);

    await I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, applicationDetailsConfig);
    await I.seeUpdatesOnCase(caseRef, caveatorDetailsTabConfig, 'caveatorApplicationDetails', applicationDetailsConfig);

    nextStepName = 'Submit application';
    endState = 'Caveat raised';
    const applicationType = 'Caveat';
    await I.logInfo(scenarioName, nextStepName, caseRef);

    // Commenting out to check the report on failed test
    // await I.chooseNextStep(nextStepName);
    await I.completeCaveatApplicationPage1();
    await I.completeCaveatApplicationPage2(caseRef);
    // await I.completeCaveatApplicationPage3();

    await I.logInfo(scenarioName, 'Payment');
    await I.makeCaveatPaymentPage1(caseRef, serviceRequestTabConfig);
    await I.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
    await I.makePaymentPage2(caseRef);
    await I.viewPaymentStatus(caseRef, applicationType);

    await I.seeEndState(endState);

    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    completeApplicationConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
    // When emailing the caveator, the Date added for the email document is set to today
    completeApplicationConfig.notification_date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));

    //await I.seeCaseDetails(caseRef, paymentDetailsTabConfig, completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, caveatDetailsTabConfig, 'completedApplication', completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, notificationsTabConfig, 'completedApplication', completeApplicationConfig);
}).tag('@crossbrowser')
    .retry(testConfig.TestRetryScenarios);
