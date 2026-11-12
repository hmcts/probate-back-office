import { BasePage } from "../../../Pages/utility/basePage.ts";
import { SignInPage } from "../../../Pages/IDAM/signIn.ts";
import { CreateCasePage } from "../../../Pages/newCase/newCase.ts";
import { SolCreateCasePage } from "../../../Pages/newCase/solNewCase.ts";
import { CwEventActionsPage } from "../../../Pages/newCase/cwEventActions.ts";
import createCaseConfig from "../../../Pages/createCase/createCaseConfig.json" with { type: "json" };
import deceasedDetailsConfig from "../../../Pages/solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };

interface SolicitorCaseFixtures {
  basePage: BasePage;
  signInPage: SignInPage;
  createCasePage: CreateCasePage;
  solCreateCasePage: SolCreateCasePage;
  cwEventActionsPage: CwEventActionsPage;
}

export async function createSolicitorCase(
  { basePage, signInPage, createCasePage, solCreateCasePage, cwEventActionsPage }: SolicitorCaseFixtures,
  caseTypeKey: string
): Promise<string> {
  const scenarioName = 'Solicitor - Case Creation';
  const isProbate = !['admon', 'intestacy'].includes(caseTypeKey);

  await basePage.logInfo(scenarioName, 'Login as Solicitor');
  await signInPage.authenticateWithIdamIfAvailable(true);

  let nextStepName = 'Apply for probate';
  await basePage.logInfo(scenarioName, nextStepName);
  await createCasePage.selectNewCase();
  await createCasePage.selectCaseTypeOptions(createCaseConfig.list2_text_gor, createCaseConfig.list3_text_solGor);
  await solCreateCasePage.applyForProbatePage1(isProbate);
  await solCreateCasePage.applyForProbatePage2(isProbate, isProbate);
  await solCreateCasePage.cyaPage();
  await solCreateCasePage.seeEndState('Application created (deceased details)');

  const caseRef = await basePage.getCaseRefFromUrl();
  await basePage.logInfo(scenarioName, 'Case created', caseRef);

  nextStepName = 'Deceased details';
  await basePage.logInfo(scenarioName, nextStepName, caseRef);
  await cwEventActionsPage.chooseNextStep(nextStepName);
  const willTypeMap: Record<string, string> = { admon: 'WillLeftAnnexed', intestacy: 'NoWill' };
  const willType = willTypeMap[caseTypeKey] ?? 'WillLeft';
  await solCreateCasePage.deceasedDetailsPage1();
  await solCreateCasePage.deceasedDetailsPage2(undefined, undefined, 'IHT207');
  await solCreateCasePage.provideIhtValues(deceasedDetailsConfig.page2_ihtGrossValue, deceasedDetailsConfig.page2_ihtNetValue, 'IHT207');
  await solCreateCasePage.deceasedDetailsPage3(willType);
  if (isProbate) {
    await solCreateCasePage.deceasedDetailsPage4();
  }

  const endStateMap: Record<string, string> = { admon: 'Admon will grant created', intestacy: 'Intestacy grant created' };
  await solCreateCasePage.cyaPage();
  await solCreateCasePage.seeEndState(endStateMap[caseTypeKey] ?? 'Grant of probate created');

  if (caseTypeKey === 'admon') {
    nextStepName = 'Admon will details';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.admonWillDetailsPage1();
    await solCreateCasePage.admonWillDetailsPage2(true);
    await solCreateCasePage.admonWillDetailsPage3();
    await solCreateCasePage.admonWillDetailsPage4();
    await solCreateCasePage.admonWillDetailsPage5();
    await solCreateCasePage.cyaPage();
    await solCreateCasePage.seeEndState('Application updated');

  } else if (caseTypeKey === 'intestacy') {
    nextStepName = 'Intestacy details';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.intestacyDetailsPage1();
    await solCreateCasePage.intestacyDetailsPage2();
    await solCreateCasePage.intestacyDetailsPage3();
    await solCreateCasePage.intestacyDetailsPage4();
    await solCreateCasePage.cyaPage();
    await solCreateCasePage.seeEndState('Application updated');

  } else {
    nextStepName = 'Grant of probate details';
    await basePage.logInfo(scenarioName, nextStepName, caseRef);
    await cwEventActionsPage.chooseNextStep(nextStepName);
    await solCreateCasePage.grantOfProbatePage1();
    await solCreateCasePage.grantOfProbatePage2(true, isProbate, isProbate);
    await solCreateCasePage.grantOfProbatePage4(false);
    await solCreateCasePage.grantOfProbatePage5();
    await solCreateCasePage.grantOfProbatePage6();
    await solCreateCasePage.cyaPage();
    await solCreateCasePage.seeEndState('Application updated');
  }

  nextStepName = 'Complete application';
  await basePage.logInfo(scenarioName, nextStepName, caseRef);
  await cwEventActionsPage.chooseNextStep(nextStepName);
  await solCreateCasePage.completeApplicationPage1(willType);
  await solCreateCasePage.completeApplicationPage3();
  await solCreateCasePage.completeApplicationPage4();
  await solCreateCasePage.completeApplicationPage5();
  await solCreateCasePage.completeApplicationPage6();
  await solCreateCasePage.completeApplicationPage7();
  await solCreateCasePage.completeApplicationPage8();

  return caseRef;
}
