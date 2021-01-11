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
const paymentDetailsTabConfig = require('src/test/end-to-end/pages/caseDetails/solicitorApplyCaveat/paymentDetailsTabConfig');

Feature('Solicitor - Apply Caveat').retry(testConfig.TestRetryFeatures);

Scenario('Solicitor - Apply Caveat', async function (I) {

    // IdAM
    await I.authenticateWithIdamIfAvailable();

    let nextStepName = 'Application details';
    let endState = 'Caveat created';
    await I.selectNewCase();
    await I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
    await I.applyCaveatPage1();
    await I.applyCaveatPage2();
    await I.cyaPage();

    await I.seeEndState(endState);

    const url = await I.grabCurrentUrl();
    const caseRef = url.split('/').pop()
        .match(/.{4}/g)
        .join('-');

    // eslint-disable-next-line no-console
    console.log('url is...', url);

    await I.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState);
    await I.seeCaseDetails(caseRef, caseDetailsTabConfig, applyCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatorDetailsTabConfig, applyCaveatConfig);
    await I.seeCaseDetails(caseRef, caveatDetailsTabConfig, applyCaveatConfig);
    await I.seeCaseDetails(caseRef, notificationsTabConfig, {});

    endState = 'Caveat updated';
    await I.chooseNextStep(nextStepName);
    await I.caveatApplicationDetailsPage1();
    await I.caveatApplicationDetailsPage2();
    await I.cyaPage();

    await I.seeEndState(endState);

    await I.seeCaseDetails(caseRef, deceasedDetailsTabConfig, applicationDetailsConfig);
    await I.seeUpdatesOnCase(caseRef, caveatorDetailsTabConfig, 'caveatorApplicationDetails', applicationDetailsConfig);

    nextStepName = 'Complete application';
    endState = 'Caveat raised';
    await I.chooseNextStep(nextStepName);
    await I.completeCaveatApplicationPage1();
    await I.completeCaveatApplicationPage2();
    await I.completeCaveatApplicationPage3();

    await I.seeEndState(endState);

    // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
    completeApplicationConfig.caveat_expiry_date = dateFns.format(dateFns.addMonths(new Date(), 6), 'D MMM YYYY');
    // When emailing the caveator, the Date added for the email document is set to today
    completeApplicationConfig.notification_date = dateFns.format(new Date(), 'D MMM YYYY');

    await I.seeCaseDetails(caseRef, paymentDetailsTabConfig, completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, caveatDetailsTabConfig, 'completedApplication', completeApplicationConfig);
    await I.seeUpdatesOnCase(caseRef, notificationsTabConfig, 'completedApplication', completeApplicationConfig);
});
