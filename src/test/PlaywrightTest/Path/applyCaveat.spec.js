'use strict';
const { test } = require('../Fixtures/createFixtures');
const dateFns = require('date-fns');

const createCaseConfig = require('../Pages/createCase/createCaseConfig');

const applicationDetailsConfig = require('../Pages/solicitorApplyCaveat/applicationDetails/applicationDetails');
const applyCaveatConfig = require('../Pages/solicitorApplyCaveat/applyCaveat/applyCaveat');
const completeApplicationConfig = require('../Pages/solicitorApplyCaveat/completeApplication/completeApplication');

const historyTabConfig = require('../Pages/caseDetails/caveat/historyTabConfig.json');

const caseDetailsTabConfig = require('../Pages/caseDetails/caveat/caseDetailsTabConfig.json');
const caveatorDetailsTabConfig = require('../Pages/caseDetails/caveat/caveatorDetailsTabConfig.json');
const caveatDetailsTabConfig = require('../Pages/caseDetails/caveat/caveatDetailsTabConfig.json');
const notificationsTabConfig = require('../Pages/caseDetails/solicitorApplyCaveat/notificationsTabConfig');
const deceasedDetailsTabConfig = require('../Pages/caseDetails/solicitorApplyCaveat/deceasedDetailsTabConfig');
const serviceRequestTabConfig = require('../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Solicitor - Apply Caveat', () => {
    test('Solicitor - Apply Caveat', async ({basePage, signInPage, createCasePage}) => {
        let scenarioName = 'Solicitor - Apply Caveat';

        await basePage.logInfo(scenarioName, 'Login as Caseworker');
        await signInPage.authenticateWithIdamIfAvailable(false);

        let nextStepName = 'Application details';
        let endState = 'Caveat created';

        // Create a caveat
        await basePage.logInfo(scenarioName, nextStepName);
        await createCasePage.selectNewCase();
        await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat)
        await createCasePage.applyCaveatPage1;
        await createCasePage.applyCaveatPage2;
        await createCasePage.cyaPage;
        await createCasePage.seeEndState;

        const caseRef = await page.getCaseRefFromUrl();
        await page.seeCaseDetails(caseRef, historyTabConfig, {}, nextStepName, endState)
        await page.seeCaseDetails(caseRef, caseDetailsTabConfig, applyCaveatConfig);
        await page.seeCaseDetails(caseRef, caveatorDetailsTabConfig, applyCaveatConfig);
        await page.seeCaseDetails(caseRef, caveatDetailsTabConfig, applyCaveatConfig);

        // Update a caveat
        endState = 'Caveat updated';
        await page.logInfo(scenarioName, nextStepName, caseRef);
        await page.chooseNextStep(nextStepName);
        await page.caveatApplicationDetailsPage1();
        await page.caveatApplicationDetailsPage2();
        await page.cyaPage;


        await page.seeEndState(endState);

        await page.seeCaseDetails(caseRef, deceasedDetailsTabConfig, applicationDetailsConfig);
        await page.seeUpdatesOnCase(caseRef, caveatorDetailsTabConfig, 'caveatorApplicationDetails', applicationDetailsConfig);

        //Submit Application
        nextStepName = 'Submit application';
        endState = 'Caveat raised';
        const applicationType = 'Caveat';
        await page.logInfo(scenarioName, nextStepName, caseRef);
        await page.chooseNextStep(nextStepName);
        await page.completeCaveatApplicationPage1();
        await page.completeCaveatApplicationPage2(caseRef);
        // await I.completeCaveatApplicationPage3();

        await page.logInfo(scenarioName, 'Payment');
        await page.makeCaveatPaymentPage1(caseRef, serviceRequestTabConfig);
        await page.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
        await page.makePaymentPage2(caseRef);
        await page.viewPaymentStatus(caseRef, applicationType);

        await page.seeEndState(endState);

        // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
        completeApplicationConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
        // When emailing the caveator, the Date added for the email document is set to today
        completeApplicationConfig.notification_date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));

        //await I.seeCaseDetails(caseRef, paymentDetailsTabConfig, completeApplicationConfig);
        await page.seeUpdatesOnCase(caseRef, caveatDetailsTabConfig, 'completedApplication', completeApplicationConfig);
        await page.seeUpdatesOnCase(caseRef, notificationsTabConfig, 'completedApplication', completeApplicationConfig);
    });
});









