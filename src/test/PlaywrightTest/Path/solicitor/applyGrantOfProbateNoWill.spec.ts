import { test } from "../../Fixtures/fixtures.ts";

import createCaseConfig from "../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applyProbateConfig from "../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import intestacyDetailsConfig from "../../Pages/solicitorApplyProbate/intestacyDetails/intestacyDetails.json" with { type: "json" };
import completeApplicationConfig from "../../Pages/solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };

import applicantDetailsTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfig.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfig.json" with { type: "json" };
import caseDetailsTabIntestacyConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabIntestacyConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };

import sotTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/sotTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/copiesTabConfig.json" with { type: "json" };
import historyTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };
import caseProgressConfig from "../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };

test.describe("Solicitor - Apply Grant of probate - No Will (Intestacy)", () => {
  test("Solicitor - Apply Grant of probate - No Will (Intestacy)", async ({
    basePage,
    signInPage,
    createCasePage,
    solCreateCasePage,
    cwEventActionsPage
  }, testInfo) => {
    const scenarioName = 'Solicitor - Apply Grant of probate - No Will (Intestacy)';
    const willType = 'NoWill';

    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    const caseRef = await basePage.getCaseRefFromUrl();

    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, applyProbateConfig);

    nextStepName = 'Deceased details';
    endState = 'Intestacy grant created';

    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.deceasedDetailsPage1();
    await solCreateCasePage.deceasedDetailsPage2();
    await solCreateCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await solCreateCasePage.deceasedDetailsPage3('NoWill');
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig);

    nextStepName = 'Intestacy details';
    endState = 'Application updated';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.intestacyDetailsPage1();
    await solCreateCasePage.intestacyDetailsPage2();
    await solCreateCasePage.intestacyDetailsPage3();
    await solCreateCasePage.intestacyDetailsPage4();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);

    const inDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...intestacyDetailsConfig};
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, inDtlsAndDcsdDtls);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabIntestacyConfig, inDtlsAndDcsdDtls);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, sotTabConfig, willType, completeApplicationConfig);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, 'MaritalStatus', inDtlsAndDcsdDtls);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'Applicant', inDtlsAndDcsdDtls);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeApplicationPage1(willType);
    //await I.completeApplicationPage2();
    await solCreateCasePage.completeApplicationPage3();
    await solCreateCasePage.completeApplicationPage4();
    await solCreateCasePage.completeApplicationPage5();
    await solCreateCasePage.completeApplicationPage6();
    await solCreateCasePage.completeApplicationPage7();
    await solCreateCasePage.completeApplicationPage8();

    await basePage.logInfo(scenarioName, 'Payment');
    await solCreateCasePage.makePaymentPage1(caseRef, serviceRequestTabConfig);
    await solCreateCasePage.reviewPaymentDetails(caseRef, serviceRequestReviewTabConfig);
    await solCreateCasePage.makePaymentPage2(caseRef);
    await solCreateCasePage.viewPaymentStatus(testInfo, caseRef);

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, completeApplicationConfig);

  });
});
