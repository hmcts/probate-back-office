const {test} = require('../../Fixtures/fixtures');
const dateFns = require('date-fns');

// const testConfig = require('src/test/config');
const createCaseConfig = require('../../Pages/createCase/createCaseConfig');

const applyCaveatConfig = require('../../Pages/solicitorApplyCaveat/applyCaveat/applyCaveat');
const applicationDetailsConfig = require('../../Pages/solicitorApplyCaveat/applicationDetails/applicationDetails');
const completeApplicationConfig = require('../../Pages/solicitorApplyCaveat/completeApplication/completeApplication');

const historyTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/historyTabConfig');
const caseDetailsTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/caseDetailsTabConfig');
const caveatorDetailsTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/caveatorDetailsTabConfig');
const caveatDetailsTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/caveatDetailsTabConfig');
const notificationsTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/notificationsTabConfig');
const deceasedDetailsTabConfig = require('../../Pages/caseDetails/solicitorApplyCaveat/deceasedDetailsTabConfig');
const serviceRequestTabConfig = require('../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig');
const serviceRequestReviewTabConfig = require('../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig');

const {
    legacyParse,
    convertTokens
} = require('@date-fns/upgrade/v2');

test.describe('Solicitor - Apply Caveat', () => {
    test('Solicitor - Apply Caveat',
        async ({basePage, signInPage, createCasePage, solCreateCasePage, cwEventActionsPage}) => {
            const scenarioName = 'Solicitor - Apply Caveat';

            await basePage.logInfo(scenarioName, 'Login as Solicitor');
            await signInPage.authenticateWithIdamIfAvailable(true);

            let nextStepName = 'Application details';
            let endState = 'Caveat created';
            await basePage.logInfo(scenarioName, nextStepName);
            await createCasePage.selectNewCase();
            await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_caveat, createCaseConfig.list3_text_caveat);
            await solCreateCasePage.applyCaveatPage1();
            await solCreateCasePage.applyCaveatPage2();
            await solCreateCasePage.cyaPage();

            await solCreateCasePage.seeEndState(endState);

            const caseRef = await basePage.getCaseRefFromUrl();

            await basePage.seeCaseDetails(caseRef, historyTabConfig, {}, createCaseConfig.list3_text_caveat, endState);
            await basePage.seeCaseDetails(caseRef, caseDetailsTabConfig, applyCaveatConfig);
            await basePage.seeCaseDetails(caseRef, caveatorDetailsTabConfig, applyCaveatConfig);
            await basePage.seeCaseDetails(caseRef, caveatDetailsTabConfig, applyCaveatConfig);

            endState = 'Caveat updated';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await solCreateCasePage.caveatApplicationDetailsPage1();
            await solCreateCasePage.caveatApplicationDetailsPage2();
            await solCreateCasePage.cyaPage();

            await solCreateCasePage.seeEndState(endState);

            await basePage.seeCaseDetails(caseRef, deceasedDetailsTabConfig, applicationDetailsConfig);
            await basePage.seeUpdatesOnCase(caseRef, caveatorDetailsTabConfig, 'caveatorApplicationDetails', applicationDetailsConfig);

            nextStepName = 'Submit application';
            endState = 'Caveat raised';
            const applicationType = 'Caveat';
            await basePage.logInfo(scenarioName, nextStepName, caseRef);
            await cwEventActionsPage.chooseNextStep(nextStepName);
            await solCreateCasePage.completeCaveatApplicationPage1();
            await solCreateCasePage.completeCaveatApplicationPage2(caseRef);
            // await I.completeCaveatApplicationPage3();

            await basePage.logInfo(scenarioName, 'Payment');
            await solCreateCasePage.makeCaveatPaymentPage1(caseRef, serviceRequestTabConfig);
            await solCreateCasePage.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
            await solCreateCasePage.makePaymentPage2(caseRef);
            await solCreateCasePage.viewPaymentStatus(caseRef, applicationType);

            await solCreateCasePage.seeEndState(endState);

            // When raising a caveat, Caveat Expiry Date is automatically set to today + 6 months
            completeApplicationConfig.caveat_expiry_date = dateFns.format(legacyParse(dateFns.addMonths(new Date(), 6)), convertTokens('D MMM YYYY'));
            // When emailing the caveator, the Date added for the email document is set to today
            completeApplicationConfig.notification_date = dateFns.format(legacyParse(new Date()), convertTokens('D MMM YYYY'));

            //await I.seeCaseDetails(caseRef, paymentDetailsTabConfig, completeApplicationConfig);
            await basePage.seeUpdatesOnCase(caseRef, caveatDetailsTabConfig, 'completedApplication', completeApplicationConfig);
            await basePage.seeUpdatesOnCase(caseRef, notificationsTabConfig, 'completedApplication', completeApplicationConfig);
        });
});
