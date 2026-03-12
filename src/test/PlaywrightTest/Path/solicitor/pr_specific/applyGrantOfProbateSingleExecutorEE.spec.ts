import dotenv from "dotenv";
dotenv.config();

import { test } from '../../../Fixtures/index.ts';
import { testConfig } from "../../../Configs/config.ts";

import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };

import applyProbateConfig from "../../../Pages/solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import gopConfig from "../../../Pages/solicitorApplyProbate/grantOfProbate/grantOfProbate.json" with { type: "json" };
import deceasedDetailsConfig from "../../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import completeApplicationConfig from "../../../Pages/solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };

import applicantDetailsTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/applicantDetailsTabConfig.json" with { type: "json" };
import applicantExecutorDetailsTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/applicantExecDetailsTrustCorpTabConfig.json" with { type: "json" };
import deceasedTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/deceasedTabConfigEE.json" with { type: "json" };
import iHTTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/iHTTabConfigEE400.json" with { type: "json" };
import caseDetailsTabDeceasedDtlsConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabDeceasedDtlsConfigEE.json" with { type: "json" };
import caseDetailsTabGopConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabGopTrustCorpConfig.json" with { type: "json" };
import caseDetailsTabUpdatesConfig from "../../../Pages/caseDetails/solicitorApplyProbate/caseDetailsTabUpdatesConfig.json" with { type: "json" };
import sotTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/sotTabConfig.json" with { type: "json" };
import copiesTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/copiesTabConfig.json" with { type: "json" };
import historyTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/historyTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import serviceRequestReviewTabConfig from "../../../Pages/caseDetails/solicitorApplyProbate/serviceRequestReviewTabConfig.json" with { type: "json" };
import caseProgressConfig from "../../../Pages/caseProgressStandard/caseProgressConfig.json" with { type: "json" };
import refundConfig from "../../../Pages/solicitorApplyProbate/makePayment/refundConfig.json" with { type: "json" };
import { refundReviewConfig } from "../../../Pages/solicitorApplyProbate/makePayment/refundReviewConfig.ts";
import {getAccessToken, getServiceAuthToken, getServiceAuthTokenforLiberata} from "../../../Pages/utility/apiHelper.ts";

test.describe.serial("Solicitor - Apply Grant of probate Excepted Estates and Refunds", () => {
  let caseRef;
  let caseRefApi;
  let env: string, serviceAuthToken: string, s2sUrl: string, authToken: string, idamUrl: string;

 test("Solicitor - Apply Grant of probate Single Executor for Excepted Estates", async ({
      basePage,
      signInPage,
      createCasePage,
      solCreateCasePage,
      cwEventActionsPage,
      callback
    }, testInfo) => {
    test.setTimeout(300000);
    const scenarioName = 'Solicitor - Apply Grant of probate Single Executor for Excepted Estates';
    const isSolicitorNamedExecutor = true;
    const isSolicitorApplyingExecutor = true;
    const willType = 'WillLeft';

    await basePage.logInfo(scenarioName, 'Login as Solicitor');
    await signInPage.authenticateWithIdamIfAvailable(true);

    let nextStepName = 'Apply for probate';
    let endState = 'Application created (deceased details)';
    await basePage.logInfo(scenarioName, nextStepName);
    await createCasePage.selectNewCase();
    await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
    await solCreateCasePage.applyForProbatePage1();
    await solCreateCasePage.applyForProbatePage2(isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);

    caseRef = await basePage.getCaseRefFromUrl();

    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantDetailsTabConfig, applyProbateConfig);

    nextStepName = 'Deceased details';
    endState = 'Grant of probate created';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.deceasedDetailsPage1('EE');
    await solCreateCasePage.deceasedDetailsPage2('EE', 'Yes', 'IHT400');
    await solCreateCasePage.enterIhtDetails(caseProgressConfig, caseProgressConfig.optionYes);
    await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT400');
    await solCreateCasePage.deceasedDetailsPage3();
    await solCreateCasePage.deceasedDetailsPage4();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, deceasedTabConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, deceasedDetailsConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, iHTTabConfig, deceasedDetailsConfig);
    await basePage.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, deceasedDetailsConfig, false);

    nextStepName = 'Grant of probate details';
    endState = 'Application updated';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2('true', isSolicitorNamedExecutor, isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage3();
    await solCreateCasePage.grantOfProbatePage4(isSolicitorApplyingExecutor);
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();

    await solCreateCasePage.seeEndState(endState);
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);

    const gopDtlsAndDcsdDtls = {...deceasedDetailsConfig, ...gopConfig};
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabDeceasedDtlsConfig, gopDtlsAndDcsdDtls);
    await basePage.seeCaseDetails(testInfo, caseRef, caseDetailsTabGopConfig, gopDtlsAndDcsdDtls);
    await basePage.seeUpdatesOnCase(testInfo, caseRef, caseDetailsTabUpdatesConfig, willType, gopDtlsAndDcsdDtls, true);
    await basePage.dontSeeCaseDetails(caseDetailsTabDeceasedDtlsConfig.fieldsNotPresent);

    await basePage.seeUpdatesOnCase(testInfo, caseRef, applicantDetailsTabConfig, 'SolicitorMainApplicantAndExecutor', applyProbateConfig, false);
    await basePage.seeCaseDetails(testInfo, caseRef, sotTabConfig, completeApplicationConfig);

    nextStepName = 'Complete application';
    endState = 'Awaiting documentation';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.completeApplicationPage1();
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

    caseRefApi = await basePage.getCaseRefFromUrlNoHyphen();
    await basePage.seeCaseDetails(testInfo, caseRef, historyTabConfig, {}, nextStepName, endState);
    await basePage.seeCaseDetails(testInfo, caseRef, copiesTabConfig, completeApplicationConfig);
    await basePage.seeCaseDetails(testInfo, caseRef, applicantExecutorDetailsTabConfig, gopDtlsAndDcsdDtls);
    await signInPage.signOut();

    await Promise.all([
      (async () => {
        env = await basePage.getEnv();
        idamUrl = `https://idam-api.${env}.platform.hmcts.net`;
        s2sUrl = `http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal/testing-support/lease`;
        authToken = await getAccessToken(idamUrl, testConfig.TestEnvCwUser, testConfig.TestEnvCwPassword);
        serviceAuthToken = await getServiceAuthToken(s2sUrl);
        await callback.backdatePayment(env, caseRefApi, authToken, serviceAuthToken, '5');
     })()
   ]);
  });

  test("Add Remission for HWF and Approve Refund", async ({
      basePage,
      signInPage,
      solCreateCasePage,
      callback,
      cwEventActionsPage
    }) => {
    const scenarioName = 'Add Remission for HWF and Approve Refund';
    // caseRef = '1772-4480-5153-5727';
    // let remissionRefundRef = 'RF-1772-4481-7720-5544';
    const nextStepName = 'Add Remission';

    // log in as requestor case worker
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef);

    await basePage.logInfo(scenarioName, 'Initiate refund request for remission');
    const remissionRefundRef = await cwEventActionsPage.addRemissionAndRefund(caseRef);
    await solCreateCasePage.navigateToCase(caseRef);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef);

    await basePage.logInfo(scenarioName, 'Initiate refund request');
    const refundRef = await cwEventActionsPage.issueRefundRequest(caseRef);
    await signInPage.signOut();

    //log in as team leader
    await basePage.logInfo(scenarioName, 'Approve remission refund request as a team leader');
    await signInPage.authenticateWithIdamIfAvailable('superUser');
    await solCreateCasePage.navigateToCase(caseRef);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, remissionRefundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund('Initiated', remissionRefundRef, refundReviewConfig.rows, true);
    //await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, remissionRefundRef);
    await cwEventActionsPage.submitRefundProcess(caseRef, refundConfig.refundProcessApprove, true);
    await cwEventActionsPage.verifyRefundConfirmation(refundConfig.refundApprovedConfirmationText);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, remissionRefundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund(refundConfig.refundStatus2, remissionRefundRef, refundReviewConfig.rows, true);

    await basePage.logInfo(scenarioName, 'Return the refund request to caseworker as a team leader');
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, refundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund('Initiated', refundRef, refundReviewConfig.rows);
    await cwEventActionsPage.submitRefundProcess(caseRef, refundConfig.refundProcessReturnToCw);
    await cwEventActionsPage.verifyRefundConfirmation(refundConfig.refundReturnToCwConfirmationText);
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Respond to refund request as a requestor caseworker');
    await signInPage.authenticateWithIdamIfAvailable(false, testConfig.CaseProgressSignInDelay);
    await solCreateCasePage.navigateToCase(caseRef);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, refundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund(refundConfig.refundStatus3, refundRef, refundReviewConfig.rows);
    await cwEventActionsPage.submitRefundProcess(caseRef, 'changeRefundDetails');
    await signInPage.signOut();

    await basePage.logInfo(scenarioName, 'Reject refund request as team leader');
    await signInPage.authenticateWithIdamIfAvailable('superUser');
    await solCreateCasePage.navigateToCase(caseRef);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, refundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund('Initiated', refundRef, refundReviewConfig.rows, false, true);
    await cwEventActionsPage.submitRefundProcess(caseRef, refundConfig.refundProcessReject, false, true);
    await cwEventActionsPage.verifyRefundConfirmation(refundConfig.refundRejectedText);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, refundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund(refundConfig.refundStatus4, refundRef, refundReviewConfig.rows, false, true);

    serviceAuthToken = await getServiceAuthTokenforLiberata(s2sUrl);
    await callback.refundsApprovalLiberata(env, remissionRefundRef, serviceAuthToken);
    await solCreateCasePage.reviewPaymentDetailsForRefund(caseRef, true, remissionRefundRef);
    await cwEventActionsPage.verifyAndInitiateProcessRefund(refundConfig.refundStatus5, remissionRefundRef, refundReviewConfig.rows, true, true);
    await signInPage.signOut();

  });


});
