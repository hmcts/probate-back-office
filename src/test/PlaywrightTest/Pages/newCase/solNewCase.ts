import { expect, Page, TestInfo } from "@playwright/test";
import dateFns from "date-fns";
import { testConfig } from "../../Configs/config.ts";
import postPaymentReviewTabConfig from "../caseDetails/solicitorApplyProbate/postPaymentReviewTabConfig.json" with { type: "json" };
import makeCardPaymentConfig from "../solicitorApplyProbate/makePayment/makeCardPaymentConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import commonConfig from "../common/commonConfig.json" with { type: "json" };
import createCaveatConfig from "../createCaveat/createCaveatConfig.json" with { type: "json" };
import applicationDetailsConfig from "../solicitorApplyCaveat/applicationDetails/applicationDetails.json" with { type: "json" };
import applyCaveatConfig from "../solicitorApplyCaveat/applyCaveat/applyCaveat.json" with { type: "json" };
import deceasedDetailsConfig from "../solicitorApplyProbate/deceasedDetails/deceasedDetailsConfig.json" with { type: "json" };
import completeApplicationConfig from "../solicitorApplyCaveat/completeApplication/completeApplication.json" with { type: "json" };
import applyProbateConfig from "../solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import makePaymentConfig from "../solicitorApplyProbate/makePayment/makePaymentConfig.json" with { type: "json" };
import grantOfProbateConfig from "../solicitorApplyProbate/grantOfProbate/grantOfProbate.json" with { type: "json" };
import completeProbateApplicationConfig from "../solicitorApplyProbate/completeApplication/completeApplication.json" with { type: "json" };
import intestacyDetailsConfig from "../solicitorApplyProbate/intestacyDetails/intestacyDetails.json" with { type: "json" };
import admonWillDetailsConfig from "../solicitorApplyProbate/admonWillDetails/admonWillDetails.json" with { type: "json" };
import shareCaseConfig from "../shareCase/shareCaseConfig.json" with { type: "json" };
import { BasePage } from "../utility/basePage.ts";
import nocConfig from "../noticeOfChange/noticeOfChangeConfig.json" with { type: "json" };

type ServiceRequestTabConfig = typeof serviceRequestTabConfig;

export class SolCreateCasePage extends BasePage {
  readonly deceasedForenameLocator = this.page.locator("#deceasedForenames");
  readonly deceasedSurnameLocator = this.page.locator("#deceasedSurname");
  readonly deceasedDobDayLocator = this.page.locator("#deceasedDateOfBirth-day");
  readonly deceasedDobMonthLocator = this.page.locator("#deceasedDateOfBirth-month");
  readonly deceasedDobYearLocator = this.page.locator("#deceasedDateOfBirth-year");
  readonly deceasedDomicileLocator = this.page.locator(`#deceasedDomicileInEngWales_${deceasedDetailsConfig.optionYes}`);
  readonly deceasedDateOfDeathDayLocator = this.page.locator("#deceasedDateOfDeath-day");
  readonly deceasedDateOfDeathMonthLocator = this.page.locator("#deceasedDateOfDeath-month");
  readonly deceasedDateOfDeathYearLocator = this.page.locator("#deceasedDateOfDeath-year");
  readonly deceasedAliasNamesLocator = this.page.locator(`#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`);
  readonly deceasedNoAliasNamesLocator = this.page.locator(`#deceasedAnyOtherNames_${deceasedDetailsConfig.optionNo}`);
  readonly deceasedAddressLine1Locator = this.page.locator("#deceasedAddress__detailAddressLine1");
  readonly deceasedAddressLine2Locator = this.page.locator("#deceasedAddress__detailAddressLine2");
  readonly deceasedAddressLine3Locator = this.page.locator("#deceasedAddress__detailAddressLine3");
  readonly deceasedAddressTownLocator = this.page.locator("#deceasedAddress__detailPostTown");
  readonly deceasedAddressCountyLocator = this.page.locator("#deceasedAddress__detailCounty");
  readonly deceasedAddressPostCodeLocator = this.page.locator("#deceasedAddress__detailPostCode");
  readonly deceasedAddressCountryLocator = this.page.locator("#deceasedAddress__detailCountry");
  readonly completeApplicationSubmitButton = this.page.getByRole("button", {name: "Close and return to case details",});
  readonly serviceRequestTabLocator = this.page.getByRole("tab", {name: makePaymentConfig.paymentTab,});
  readonly reviewLinkLocator = this.page.getByText(makePaymentConfig.reviewLinkText);
  readonly backToServiceRequestLocator = this.page.getByRole("link", {
    name: makePaymentConfig.backToPaymentLinkText,
    exact: true,
  });
  readonly payNowLinkLocator = this.page.getByRole("link", {
    name: makePaymentConfig.payNowLinkText,
    exact: true,
  });
  readonly pbaOptionLocator = this.page.locator("#pbaAccount");
  readonly pbaAccountNumberLocator = this.page.locator("#pbaAccountNumber");
  readonly confirmPaymentButton = this.page.getByRole("button", {name: "Confirm payment"});
  readonly continuePaymentButton = this.page.getByRole("button", {name: "Continue"});
  readonly serviceRequestLinkLocator = this.page.getByRole("link", {
    name: makePaymentConfig.serviceRequestLink,
    exact: true,
  });
  readonly cardServiceRequestLinkLocator = this.page.getByRole("link", {
    name: makeCardPaymentConfig.serviceRequestLink,
    exact: true,
  });
  readonly eventHistoryTab = this.page.getByRole("tab", {name: makePaymentConfig.eventHistoryTab,});
  readonly caseProgressTabLocator = this.page.getByRole("tab", {
    name: makePaymentConfig.caseProgressTab
  });
  readonly postcodeLinkLocator = this.page.getByText(createCaveatConfig.UKpostcodeLink);
  readonly solSignSot = this.page.locator(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);
  readonly solsStartPageLocator = this.page.locator('#solsStartPage');
  readonly solsApplyPageLocator = this.page.locator('#solsApplyPage');
  readonly solsPageSubHeading = this.page.getByText(applyProbateConfig.page2_subheading);
  readonly solsHelp = this.page.getByText(applyProbateConfig.page2_probatePractionerHelp);
  readonly solForenameLocator = this.page.locator('#solsForenames');
  readonly solSurnameLocator = this.page.locator('#solsSurname');
  readonly solSotForenameLocator = this.page.locator('#solsSOTForenames');
  readonly solSotSurnameLocator = this.page.locator('#solsSOTSurname');
  readonly solsIsExecLocator = this.page.locator('#solsSolicitorIsExec_Yes');
  readonly applyForProbateHintLocator = this.page.locator('#applyForProbatePageHint1');
  readonly solsApplyingLocator = this.page.locator('#solsSolicitorIsApplying_Yes');
  readonly solsNotApplyingLocator = this.page.locator('#solsSolicitorIsApplying_No');
  readonly solsNotApplyReasonLocator = this.page.locator('#solsSolicitorNotApplyingReason-PowerReserved');
  readonly solsIsNotExecLocator = this.page.locator('#solsSolicitorIsExec_No');
  readonly applyForProbateHint2Locator = this.page.locator('#applyForProbatePageHint2');
  readonly solsFirmNameLocator = this.page.locator('#solsSolicitorFirmName');
  readonly solsAddressLine1Locator = this.page.locator('#solsSolicitorAddress__detailAddressLine1');
  readonly solsAddressLine2Locator = this.page.locator('#solsSolicitorAddress__detailAddressLine2');
  readonly solsAddressLine3Locator = this.page.locator('#solsSolicitorAddress__detailAddressLine3');
  readonly solsPostTownLocator = this.page.locator('#solsSolicitorAddress__detailPostTown');
  readonly solsCountyLocator = this.page.locator('#solsSolicitorAddress__detailCounty');
  readonly solsPostcodeLocator = this.page.locator('#solsSolicitorAddress__detailPostCode');
  readonly solsCountryLocator = this.page.locator('#solsSolicitorAddress__detailCountry');
  readonly solsEmailLocator = this.page.locator('#solsSolicitorEmail');
  readonly solsPhoneLocator = this.page.locator('#solsSolicitorPhoneNumber');
  readonly solsAppReferenceLocator = this.page.locator('#solsSolicitorAppReference');
  readonly ihtFormEstateValueCompleted = this.page.locator(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionYes}`);
  readonly ihtFormsLabelLocator = this.page.getByText(deceasedDetailsConfig.page2_whichIHTFormsLabel);
  readonly iht207Locator = this.page.locator('#ihtFormEstate').getByText(deceasedDetailsConfig.page2_IHT207Label);
  readonly iht400Locator = this.page.locator('#ihtFormEstate').getByText(deceasedDetailsConfig.page2_IHT400Label, { exact: true });
  readonly iht207OptionLocator = this.page.locator(`#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE207}`);
  readonly iht400OptionLocator = this.page.locator(`#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE400}`);
  readonly iht400421OptionLocator = this.page.locator(`#ihtFormEstate-${deceasedDetailsConfig.page2_IHTOptionEE400421}`);
  readonly ihtFormEstateValueNotCompleted = this.page.locator(`#ihtFormEstateValuesCompleted_${deceasedDetailsConfig.optionNo}`);
  readonly ihtGrossValueLabelLocator = this.page.getByText(deceasedDetailsConfig.page2_grossValueIHTEstateLabel);
  readonly ihtNetValueLabelLocator = this.page.getByText(deceasedDetailsConfig.page2_netValueIHTEstateLabel);
  readonly ihtNetQualifyingValueLabelLocator = this.page.getByText(deceasedDetailsConfig.page2_netQualifyingValueIHTEstateLabel);
  readonly ihtEstateGrossValueLocator = this.page.locator('#ihtEstateGrossValue');
  readonly ihtEstateNetValueLocator = this.page.locator('#ihtEstateNetValue');
  readonly ihtEstateNetQualifyingValueLocator = this.page.locator('#ihtEstateNetQualifyingValue');
  readonly deceasedLateSpouseLocator = this.page.locator(`#deceasedHadLateSpouseOrCivilPartner_${deceasedDetailsConfig.optionYes}`);
  readonly ihtUnusedAllowanceLocator = this.page.locator(`#ihtUnusedAllowanceClaimed_${deceasedDetailsConfig.optionYes}`);
  readonly formIdMultiLocator = this.page.locator(`#ihtFormId-${deceasedDetailsConfig.page2_IHTOptionMulti}`);
  readonly nilBandRateLocator = this.page.getByText(deceasedDetailsConfig.page2_NilRateBandLabel);
  readonly iht217OptionLocator = this.page.locator(`#iht217_${deceasedDetailsConfig.optionYes}`);
  readonly formIdLocator = this.page.locator(`#ihtFormId-${deceasedDetailsConfig.page2_IHTOption}`);
  readonly ihtGrossValueLocator = this.page.locator('#ihtGrossValue');
  readonly ihtFormNetValueLocator = this.page.locator('#ihtFormNetValue');
  readonly ihtNetValueLocator = this.page.locator('#ihtNetValue');
  readonly solsWillTypeLocator = this.page.locator('#solsWillType');
  readonly willDisposeLocator = this.page.locator('#willDispose');
  readonly willDisposeOptionLocator = this.page.locator(`#willDispose_${deceasedDetailsConfig.optionYes}`);
  readonly englishWillLocator = this.page.locator(`#englishWill_${deceasedDetailsConfig.optionYes}`);
  readonly appointExecLocator = this.page.locator(`#appointExec_${deceasedDetailsConfig.optionYes}`);
  readonly willAccessOriginalOptionNoLocator = this.page.locator('#willAccessOriginal_No');
  readonly noWillAccessOriginalLabelLocator = this.page.getByText(grantOfProbateConfig.page1_noAccessOriginalWillLabel);
  readonly willAccessOriginalOptionYesLocator = this.page.locator(`#willAccessOriginal_${grantOfProbateConfig.optionYes}`);
  readonly originalWillSignedDayLocator = this.page.locator('#originalWillSignedDate-day');
  readonly originalWillSignedMonthLocator = this.page.locator('#originalWillSignedDate-month');
  readonly originalWillSignedYearLocator = this.page.locator('#originalWillSignedDate-year');
  readonly willHasCodicilsLocator = this.page.locator(`#willHasCodicils_${grantOfProbateConfig.optionYes}`);
  readonly codicilAddButtonLocator = this.page.locator('#codicilAddedDateList button');
  readonly codicilAddedDayLocator = this.page.locator('#dateCodicilAdded-day');
  readonly codicilAddedMonthLocator = this.page.locator('#dateCodicilAdded-month');
  readonly codicilAddedYearLocator = this.page.locator('#dateCodicilAdded-year');
  readonly languagePreferenceWelshLocator = this.page.locator('#languagePreferenceWelsh_Yes');
  readonly languagePreferenceLabelLocator = this.page.getByText(grantOfProbateConfig.page1_languagePreferenceLabel);
  readonly dispNoticeLocator = this.page.locator(`#dispenseWithNotice_${grantOfProbateConfig.page2_dispenseWithNotice}`);
  readonly tctTypeLocator = this.page.locator('#titleAndClearingType-TCTNoT');
  readonly tctTrustCorpLocator  = this.page.locator('#titleAndClearingType-TCTTrustCorpResWithApp');
  readonly othersRenouncingLocator  = this.page.locator('#titleAndClearingType-TCTPartOthersRenouncing');
  readonly additionalApplyingPartnersLocator = this.page.locator('#anyOtherApplyingPartners_Yes');
  readonly additionalExecutorsLocator = this.page.locator('#otherPartnersApplyingAsExecutors');
  readonly noAdditionalPartnersLocator = this.page.locator('#anyOtherApplyingPartners_No');
  readonly trusCorpNameLocator = this.page.locator('#trustCorpName');
  readonly trustCorpPostcodeLinkLocator = this.page.locator(grantOfProbateConfig.page2_trustCorpPostcodeLink);
  readonly trustCorpAddressLine1Locator = this.page.locator('#trustCorpAddress__detailAddressLine1');
  readonly trustCorpAddressLine2Locator = this.page.locator('#trustCorpAddress__detailAddressLine2');
  readonly trustCorpAddressLine3Locator = this.page.locator('#trustCorpAddress__detailAddressLine3');
  readonly trustCorpPostTownLocator = this.page.locator('#trustCorpAddress__detailPostTown');
  readonly trustCorpCountyLocator = this.page.locator('#trustCorpAddress__detailCounty');
  readonly trustCorpPostcodeLocator = this.page.locator('#trustCorpAddress__detailPostCode');
  readonly trustCorpCountryLocator = this.page.locator('#trustCorpAddress__detailCountry');
  readonly anyOtherPartnersTextLocator = this.page.getByText(grantOfProbateConfig.page2_waitForAnyOtherTcPartners);
  readonly anyOtherApplyingPartnersTcLocator = this.page.locator('#anyOtherApplyingPartnersTrustCorp_Yes');
  readonly addPersonLocator = this.page.getByRole('heading', { name: grantOfProbateConfig.page2_waitForAdditionPerson, exact: true });
  readonly addExecutorsTcLocator = this.page.locator('#additionalExecutorsTrustCorpList > div > button');
  readonly addExecutorFirstnameLocator = this.page.locator('#additionalExecutorsTrustCorpList_0_additionalExecForenames');
  readonly addExecutorLastnameLocator = this.page.locator('#additionalExecutorsTrustCorpList_0_additionalExecLastname');
  readonly addExecutorTcPositionLocator = this.page.locator('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition');
  readonly probatePractitionerPositionLocator = this.page.locator('#probatePractitionersPositionInTrust');
  readonly firmNameWillTextLocator = this.page.getByText('Name of firm named in will')
  readonly firmNameInWillLocator = this.page.locator('#nameOfFirmNamedInWill');
  readonly displenseWithNoticeLeaveLocator = this.page.locator(`#dispenseWithNoticeLeaveGiven_${grantOfProbateConfig.page3_dispenseWithNoticeLeaveGiven}`);
  readonly dispenseWithNoticeOverviewLocator = this.page.locator('#dispenseWithNoticeOverview');
  readonly dispenseWithNoticeSupportingDocsLocator = this.page.locator('#dispenseWithNoticeSupportingDocs');
  readonly dispenseWithNoticeOtherExecsListLocator = this.page.locator('#dispenseWithNoticeOtherExecsList > div > button');
  readonly dispenseWithNoticeExecutorNotApplyingName = this.page.locator('#dispenseWithNoticeOtherExecsList_0_notApplyingExecutorName');
  readonly otherExecutorExistsLocator = this.page.locator('#otherExecutorExists');
  readonly otherExecutorExistsValueLocator = this.page.locator(`#otherExecutorExists_${grantOfProbateConfig.page4_otherExecutorExists}`);
  readonly additionalExecutorTextLocator = this.page.getByRole('heading', { name: grantOfProbateConfig.page2_waitForAdditionalExecutor, exact: true });
  readonly prevIdentifiedApplyingExecutors = this.page.getByText(grantOfProbateConfig.page4_previouslyIdentifiedApplyingExecutors);
  readonly prevIdentifiedNotApplyingEexecutors = this.page.getByText(grantOfProbateConfig.page4_previouslyIdentifiedNotApplyingExecutors).first();
  readonly solsAddExecutorsList = this.page.locator('#solsAdditionalExecutorList > div > button');
  readonly solsAddExectorsForenameLocator  = this.page.locator('#solsAdditionalExecutorList_0_additionalExecForenames');
  readonly solsAddExecutorLastnameLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecLastname');
  readonly solsAddExecutorNameOnWillOptionLocator = this.page.locator(`#solsAdditionalExecutorList_0_additionalExecNameOnWill_${grantOfProbateConfig.optionYes}`);
  readonly solsAddExecutorNameOnWillLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill');
  readonly solsAddExecutorsApplying = this.page.locator('#solsAdditionalExecutorList_0_additionalApplying_Yes');
  readonly solsAddExecutorApplyingPostcodeLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput');
  readonly solsAddExecutorFindAddressLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress > div  > div > button');
  readonly solsAddExecutorAddressListLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList');
  readonly solsAddExecutorAddressOptionLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList > option:first-child');
  readonly addExecAddressLocator = this.page.locator(grantOfProbateConfig.page4_postcodeLink)
  readonly addExecutorAddressLine1Locator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine1');
  readonly addExecutorAddressLine2Locator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine2');
  readonly addExecutorAddressLine3Locator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine3');
  readonly addExecutorPostTownLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostTown');
  readonly addExecutorPostCodeLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostCode');
  readonly addExecutorCountryLocator = this.page.locator('#solsAdditionalExecutorList_0_additionalExecAddress__detailCountry');
  readonly otherExecutorNotExistsLocator = this.page.locator(`#otherExecutorExists_${grantOfProbateConfig.optionNo}`);
  readonly furtherEvidenceLocator = this.page.locator('#furtherEvidenceForApplication');
  readonly additionalInfoLocator = this.page.locator('#solsAdditionalInfo');
  readonly sotUpdateRequiredLocator = this.page.locator('#solsSOTNeedToUpdate');
  readonly sotUpdateRequiredYesLocator = this.page.locator(`#solsSOTNeedToUpdate_${completeProbateApplicationConfig.optionYes}`);
  readonly sotUpdateNotRequiredLocator = this.page.locator(`#solsSOTNeedToUpdate_${completeProbateApplicationConfig.optionNo}`);
  readonly reviewLegalStatement1Locator = this.page.locator('#solsReviewLegalStatement1');
  readonly reviewLegalStatement2Locator = this.page.locator('#solsReviewLegalStatement2');
  readonly reviewLegalStatement3Locator = this.page.locator('#solsReviewLegalStatement3');
  readonly reviewLegalStatement4Locator = this.page.locator('#solsReviewLegalStatement4');
  readonly admonWillLegalStatementLink = this.page.getByText(completeProbateApplicationConfig.page1_AdmonWilllegalStmtLink);
  readonly intestacyLegalStatementLink = this.page.getByText(completeProbateApplicationConfig.page1_NoWilllegalStmtLink);
  readonly legalStatementLink = this.page.getByText(completeProbateApplicationConfig.page1_legalStmtLink);
  readonly completeApplicationWaitForText = this.page.getByRole('heading', { name: completeProbateApplicationConfig.page3_waitForText, exact: true });
  readonly sotConfirmCheck1Locator = this.page.locator('#solsReviewSOTConfirmCheckbox1-BelieveTrue');
  readonly sotConfirmCheck2Locator = this.page.locator('#solsReviewSOTConfirmCheckbox2-BelieveTrue');
  readonly extrCopiesLocator = this.page.locator('#extraCopiesOfGrant');
  readonly extraCopiesOutsideUKLocator = this.page.locator('#outsideUKGrantCopies');
  readonly solsPbaPaymentRefLocator = this.page.locator('#solsPBAPaymentReference');
  readonly serviceRequestTab = this.page.getByRole("tab", { name: makePaymentConfig.paymentTab });
  readonly reviewLocator = this.page.getByText(makePaymentConfig.reviewLinkText);
  readonly primaryApplicantForenameLocator = this.page.locator('#primaryApplicantForenames');
  readonly primaryApplicantSurnameLocator = this.page.locator('#primaryApplicantSurname');
  readonly primaryApplicantAddressLine1 = this.page.locator('#primaryApplicantAddress__detailAddressLine1');
  readonly primaryApplicantAddressLine2 = this.page.locator('#primaryApplicantAddress__detailAddressLine2');
  readonly primaryApplicantAddressLine3 = this.page.locator('#primaryApplicantAddress__detailAddressLine3');
  readonly primaryApplicantPostTown = this.page.locator('#primaryApplicantAddress__detailPostTown');
  readonly primaryApplicantCounty = this.page.locator('#primaryApplicantAddress__detailCounty');
  readonly primaryApplicantPostcode = this.page.locator('#primaryApplicantAddress__detailPostCode');
  readonly primaryApplicantCountry = this.page.locator('#primaryApplicantAddress__detailCountry');
  readonly primaryApplicantPhoneNumber = this.page.locator('#primaryApplicantPhoneNumber');
  readonly primaryApplicantEmail = this.page.locator('#primaryApplicantEmailAddress');
  readonly languageLocator = this.page.locator(`#languagePreferenceWelsh_${grantOfProbateConfig.optionYes}`);
  readonly solsEntitledMinority = this.page.locator(`#solsEntitledMinority_${admonWillDetailsConfig.optionNo}`);
  readonly solsDiedLocator = this.page.locator(`#solsDiedOrNotApplying_${admonWillDetailsConfig.optionYes}`);
  readonly solsResiduary = this.page.locator(`#solsResiduary_${admonWillDetailsConfig.optionYes}`);
  readonly caseListLocator = this.page.locator('//a[normalize-space()="Case list"]');
  readonly caseViewTextLocator = this.page.getByText('Your cases');
  readonly caseReferenceLocator = this.page.locator('//button[normalize-space()="Apply"]');
  readonly shareCaseButtonLocator = this.page.locator('#btn-share-button');
  readonly showAllTextLocator = this.page.locator('#accordion-with-summary-sections > div > button > span.govuk-accordion__show-all-text');
  readonly caseListHeadingLocator = this.page.getByRole('heading', { name: nocConfig.nocWaitForText });
  readonly cyaPageLocator = this.page.getByText("Check your answers");
  readonly goButtonLocator = this.page.getByRole("button", { name: "Go" });

  constructor(public readonly page: Page) {
    super(page);
  }

  async applyCaveatPage1() {
    await expect(this.page.locator("#solsCaveatEligibility")).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async applyCaveatPage2() {
    await expect(this.page.locator("#solsSolicitorFirmName")).toBeVisible();
    await this.runAccessibilityTest();
    await this.page
      .locator("#solsSolicitorFirmName")
      .fill(applyCaveatConfig.page2_firm_name);
    await this.postcodeLinkLocator.click();
    await this.page
      .locator("#caveatorAddress__detailAddressLine1")
      .fill(applyCaveatConfig.address_line1);
    await this.page
      .locator("#caveatorAddress__detailAddressLine2")
      .fill(applyCaveatConfig.address_line2);
    await this.page
      .locator("#caveatorAddress__detailAddressLine3")
      .fill(applyCaveatConfig.address_line3);
    await this.page
      .locator("#caveatorAddress__detailPostTown")
      .fill(applyCaveatConfig.address_town);
    await this.page
      .locator("#caveatorAddress__detailCounty")
      .fill(applyCaveatConfig.address_county);
    await this.page
      .locator("#caveatorAddress__detailPostCode")
      .fill(applyCaveatConfig.address_postcode);
    await this.page
      .locator("#caveatorAddress__detailCountry")
      .fill(applyCaveatConfig.address_country);
    await this.page
      .locator("#solsSolicitorAppReference")
      .fill(applyCaveatConfig.page2_app_ref);
    await this.page
      .locator("#caveatorEmailAddress")
      .fill(applyCaveatConfig.page2_caveator_email);
    await this.page
      .locator("#solsSolicitorPhoneNumber")
      .fill(applyCaveatConfig.page2_phone_num);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async cyaPage() {
    // await this.verifyPageLoad(this.cyaPageLocator);
    await expect(this.cyaPageLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.submitButton, 10_000);
  }

  async seeEndState(endState: string) {
    await expect(this.page.getByText("Event History")).toBeVisible();
    await this.page.getByRole("tab", { name: "Event History" }).focus();
    await this.page.getByRole("tab", { name: "Event History" }).click();
    await expect(this.page.getByText(endState)).toBeVisible();
  }

  async caveatApplicationDetailsPage1() {
    await expect(this.page.locator("#caveatorForenames")).toBeVisible();
    await this.runAccessibilityTest();
    await this.page
      .locator("#caveatorForenames")
      .fill(applicationDetailsConfig.page1_caveator_forename);
    await this.page
      .locator("#caveatorSurname")
      .fill(applicationDetailsConfig.page1_caveator_surname);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async caveatApplicationDetailsPage2() {
    await expect(this.deceasedForenameLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.deceasedForenameLocator.fill(applicationDetailsConfig.page2_deceased_forename);
    await this.deceasedSurnameLocator.fill(applicationDetailsConfig.page2_deceased_surname)
    await this.deceasedDateOfDeathDayLocator.fill(applicationDetailsConfig.page2_dateOfDeath_day);
    await this.deceasedDateOfDeathMonthLocator.fill(applicationDetailsConfig.page2_dateOfDeath_month);
    await this.deceasedDateOfDeathYearLocator.fill(applicationDetailsConfig.page2_dateOfDeath_year);
    await this.deceasedAliasNamesLocator.focus();
    await this.deceasedAliasNamesLocator.click();
    await this.deceasedAliasNamesLocator.click();
    if (!testConfig.TestAutoDelayEnabled) {
      // only valid for local dev where we need it to run as fast as poss to minimise
      // lost dev time
      // await this.page.waitForTimeout(testConfig.ManualDelayShort);
    }

    let idx = 0;
    const keys = Object.keys(applicationDetailsConfig);
    for (let i = 0; i < keys.length; i++) {
      const propName = keys[i];
      if (propName.includes("page2_alias_")) {
        await this.page
          .getByRole("button", {
            name: applicationDetailsConfig.page2_addAliasButton,
          })
          .first()
          .click();
        /*
        if (!testConfig.TestAutoDelayEnabled) {
          // only valid for local dev where we need it to run as fast as poss to minimise
          // lost dev time
          await this.page.waitForTimeout(testConfig.ManualDelayShort);
        }*/
        await expect(
          this.page.locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`)
        ).toBeVisible();
        await this.page
          .locator(`#deceasedFullAliasNameList_${idx}_FullAliasName`)
          .fill(applicationDetailsConfig[propName]);
        idx += 1;
      }
    }

    await this.postcodeLinkLocator.click();
    await this.deceasedAddressLine1Locator.fill(applicationDetailsConfig.address_line1);
    await this.deceasedAddressLine2Locator.fill(applicationDetailsConfig.address_line2);
    await this.deceasedAddressLine3Locator.fill(applicationDetailsConfig.address_line3);
    await this.deceasedAddressTownLocator.fill(applicationDetailsConfig.address_town);
    await this.deceasedAddressCountyLocator.fill(applicationDetailsConfig.address_county);
    await this.deceasedAddressPostCodeLocator.fill(applicationDetailsConfig.address_postcode);
    await this.deceasedAddressCountryLocator.fill(applicationDetailsConfig.address_country);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async completeCaveatApplicationPage1() {
    await this.runAccessibilityTest();
    await this.page
      .locator("#solsPBAPaymentReference")
      .fill(completeApplicationConfig.page1_paymentReference);
    await this.page
      .locator("input#paymentConfirmCheckbox-paymentAcknowledgement")
      .click();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async completeCaveatApplicationPage2(caseRef: string) {
    completeApplicationConfig.page2_notification_date = dateFns.format(
      new Date(),
      testConfig.solsDateFormat
    );
    await expect(
      this.page.getByText(completeApplicationConfig.page2_waitForText)
    ).toBeVisible();
    await this.runAccessibilityTest();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(
      this.page.getByText(completeApplicationConfig.page2_confirmationText)
    ).toBeVisible();
    await expect(
      this.page.getByText(completeApplicationConfig.page2_app_ref)
    ).toBeVisible();
    await expect(
      this.page.getByText(
        completeApplicationConfig.page2_notification_date_text
      )
    ).toBeVisible();
    await expect(
      this.page
        .getByText(completeApplicationConfig.page2_notification_date)
        .first()
    ).toBeVisible();
    await this.completeApplicationSubmitButton.click();
  }

  async makeCaveatPaymentPage1(caseRef: string, serviceRequestTabConfig: ServiceRequestTabConfig) {
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(this.serviceRequestTabLocator).toBeEnabled();
    await this.serviceRequestTabLocator.click();
    await this.runAccessibilityTest();

    for (let i = 0; i < serviceRequestTabConfig.fields.length; i++) {
      if (
        serviceRequestTabConfig.fields[i] &&
        serviceRequestTabConfig.fields[i] !== ""
      ) {
        await expect(
          this.page.getByText(serviceRequestTabConfig.fields[i])
        ).toBeVisible();
      }
    }

    await expect(this.reviewLinkLocator).toBeVisible();
    await this.reviewLinkLocator.click();
  }

  async reviewPaymentDetails(caseRef: string, serviceRequestReviewTabConfig: ServiceRequestTabConfig) {
    await this.verifyPageLoad(this.page.getByText(caseRef).first());
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(this.serviceRequestTabLocator).toBeEnabled();
    await this.runAccessibilityTest();
    for (let i = 0; i < serviceRequestReviewTabConfig.fields.length; i++) {
      if (
        serviceRequestReviewTabConfig.fields[i] &&
        serviceRequestReviewTabConfig.fields[i] !== ""
      ) {
        await expect(
          this.page.getByText(serviceRequestReviewTabConfig.fields[i]).first()
        ).toBeVisible();
      }
    }

    await expect(this.page.locator(".govuk-back-link")).toBeEnabled();
    await this.backToServiceRequestLocator.click();
  }

  async makePaymentPage2(caseRef: string) {
    await this.verifyPageLoad(this.page.getByText(caseRef).first());
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(this.payNowLinkLocator).toBeVisible();
    await this.payNowLinkLocator.click();
    await expect(
      this.page.getByText(makePaymentConfig.page2_waitForText)
    ).toBeVisible();
    await this.runAccessibilityTest();
    await expect(this.pbaOptionLocator).toBeEnabled();
    await this.pbaOptionLocator.click();
    await expect(this.pbaAccountNumberLocator).toBeEnabled();
    await this.pbaAccountNumberLocator.selectOption({
      label: makePaymentConfig.page2_pBAANumber,
    });
    await this.page
      .locator("#pbaAccountRef")
      .fill(makePaymentConfig.page2_paymentReference);
    await this.page
      .locator(
        `//label[normalize-space()="${makePaymentConfig.paymentOptionLabel}"]`
      )
      .click();
    await this.confirmPaymentButton.click();
    // await this.page.waitForTimeout(testConfig.ManualDelayLong);
  }

  async makeCardPaymentPage(caseRef) {
    await expect(this.payNowLinkLocator).toBeVisible();
    await this.payNowLinkLocator.click();
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(this.page.locator('#cardPayment')).toBeVisible();
    await this.page.locator('#cardPayment').click();
    await expect(this.continuePaymentButton).toBeEnabled();
    await this.continuePaymentButton.click();
  }

  async makeCardPaymentPage2(){
    await expect(this.page.getByRole('heading', { name: makeCardPaymentConfig.cardPaymentHeading })).toBeVisible();
    await expect(this.page.locator('#card-no')).toBeVisible();
    await this.page.locator('#card-no').fill(makeCardPaymentConfig.cardNumber);
    await this.page.locator('#expiry-month').fill(makeCardPaymentConfig.expiryMonth);
    await this.page.locator('#expiry-year').fill(makeCardPaymentConfig.expiryYear);
    await this.page.locator('#cvc').fill(makeCardPaymentConfig.cardCvc);
    await this.page.locator('#cardholder-name').fill(makeCardPaymentConfig.nameOnCard);
    await this.page.locator('#address-line-1').fill(makeCardPaymentConfig.addressLine1);
    await this.page.locator('#address-city').fill(makeCardPaymentConfig.addressTown);
    await this.page.locator('#address-postcode').fill(makeCardPaymentConfig.addressPostcode);
    await this.page.locator('#email').fill(makeCardPaymentConfig.emailAddress);
    await expect(this.continuePaymentButton).toBeEnabled();
    await this.continuePaymentButton.click();
  }

  async confirmCardDetails() {
    await expect(this.page. getByRole('heading', { name: makeCardPaymentConfig.cardVerifyPageHeading })).toBeVisible();
    await expect(this.page.getByText(makeCardPaymentConfig.nameOnCard)).toBeVisible();
    await expect(this.page.getByText(makeCardPaymentConfig.emailAddress)).toBeVisible();
    await this.confirmPaymentButton.click();
  }

  async viewCardPaymentStatus(caseRef: string) {
    await expect(
      this.page.getByText(makePaymentConfig.paymentStatusConfirmText)
    ).toBeVisible();
    await expect(this.cardServiceRequestLinkLocator).toBeEnabled();
    await this.cardServiceRequestLinkLocator.click();
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(
      this.page.getByText(makePaymentConfig.paymentStatus)
    ).toBeVisible();
    await expect(
      this.page.getByText(makePaymentConfig.payNowLinkText)
    ).toBeHidden();
    await this.postPaymentReviewDetails(caseRef);
    for (let i = 0; i <= 6; i++) {
      await expect(this.eventHistoryTab).toBeEnabled();
      await expect(this.page.getByText(caseRef).first()).toBeVisible();
      await this.eventHistoryTab.click();
      const result = await this.page
        .getByText(makePaymentConfig.statusText)
        .isVisible()
        .catch(() => true);
      await this.page.waitForTimeout(10000);
      if (result) {
        break;
      }
      await this.page.reload();
      // await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`);
    }
  }
  async viewPaymentStatus(testInfo?: TestInfo, caseRef?: string, appType?: string) {
    // await expect(this.page.getByText(caseRef).first()).toBeVisible();
    // await this.verifyPageLoad(this.page.getByText(makePaymentConfig.paymentStatusConfirmText));
    await expect(
      this.page.getByText(makePaymentConfig.paymentStatusConfirmText)
    ).toBeVisible();
    await expect(this.serviceRequestLinkLocator).toBeEnabled();
    await this.serviceRequestLinkLocator.click();
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(
      this.page.getByText(makePaymentConfig.paymentStatus)
    ).toBeVisible();
    await expect(
      this.page.getByText(makePaymentConfig.payNowLinkText)
    ).toBeHidden();
    await this.postPaymentReviewDetails(caseRef);

    for (let i = 0; i <= 6; i++) {
      await expect(this.eventHistoryTab).toBeEnabled();
      await expect(this.page.getByText(caseRef).first()).toBeVisible();
      await this.eventHistoryTab.click();
      const result = await this.page
        .getByText(makePaymentConfig.statusText)
        .isVisible()
        .catch(() => true);
      await this.page.waitForTimeout(10000);
      if (result) {
        break;
      }
      await this.page.reload();
      // await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`);
    }
    if (appType !== "Caveat") {
      await expect(this.caseProgressTabLocator).toBeVisible();
      await expect(this.page.getByText(caseRef).first()).toBeVisible();
      await this.caseProgressTabLocator.focus();
      await this.caseProgressTabLocator.click();
    }
  }

  async postPaymentReviewDetails(caseRef: string) {
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
    await expect(this.reviewLinkLocator).toBeVisible();
    await this.reviewLinkLocator.click();
    await expect(this.serviceRequestTabLocator).toBeEnabled();
    await this.runAccessibilityTest();

    for (let i = 0; i < postPaymentReviewTabConfig.fields.length; i++) {
      if (
        postPaymentReviewTabConfig.fields[i] &&
        postPaymentReviewTabConfig.fields[i] !== ""
      ) {
        await expect(
          this.page.getByText(postPaymentReviewTabConfig.fields[i]).first()
        ).toBeVisible();
        // await I.see(postPaymentReviewTabConfig.fields[i]);
      }
    }

    await expect(this.page.locator(".govuk-back-link")).toBeEnabled();
    await this.backToServiceRequestLocator.click();
  }

  async applyForProbatePage1() {
    await this.verifyPageLoad(this.solsStartPageLocator);
    await expect(this.solsStartPageLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.submitButton, 10_000);
  }

  async applyForProbatePage2(isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
    await this.verifyPageLoad(this.solsApplyPageLocator);
    await expect(this.solsApplyPageLocator).toBeVisible();
    await this.runAccessibilityTest();
    await expect(this.solsPageSubHeading).toBeVisible();
    await expect(this.solsHelp).toBeVisible();

    await expect(this.solSignSot).toBeVisible();
    await this.solSignSot.click();
    await this.solForenameLocator.fill(applyProbateConfig.page2_sol_forename);
    await this.solSurnameLocator.fill(applyProbateConfig.page2_sol_surname);
    await this.solSotForenameLocator.fill(applyProbateConfig.page2_sol_forename);
    await this.solSotSurnameLocator.fill(applyProbateConfig.page2_sol_surname);

    if (isSolicitorNamedExecutor) {
      await this.solsIsExecLocator.click();
      await expect(this.applyForProbateHintLocator).toBeVisible();

      if (isSolicitorApplyingExecutor) {
        await this.solsApplyingLocator.click();
        await expect(this.applyForProbateHintLocator).toBeVisible();
      } else {
        await this.solsNotApplyingLocator.click();
        await expect(this.solsNotApplyReasonLocator).toBeVisible();
        await this.solsNotApplyReasonLocator.click();
      }
    } else {
      await this.solsIsNotExecLocator.click();
      await this.page.locator(`#solsSolicitorIsApplying_${isSolicitorApplyingExecutor ? 'Yes' : 'No'}`).click();
      if (isSolicitorApplyingExecutor) {
        await expect(this.applyForProbateHint2Locator).toBeVisible();
      }
    }

    await this.solsFirmNameLocator.fill(applyProbateConfig.page2_firm_name);
    await this.postcodeLinkLocator.click();
    await this.solsAddressLine1Locator.fill(applyProbateConfig.address_line1);
    await this.solsAddressLine2Locator.fill(applyProbateConfig.address_line2);
    await this.solsAddressLine3Locator.fill(applyProbateConfig.address_line3);
    await this.solsPostTownLocator.fill(applyProbateConfig.address_town);
    await this.solsCountyLocator.fill(applyProbateConfig.address_county);
    await this.solsPostcodeLocator.fill(applyProbateConfig.address_postcode);
    await this.solsCountryLocator.fill(applyProbateConfig.address_country);

    await this.solsEmailLocator.fill(applyProbateConfig.page2_sol_email);
    await this.solsPhoneLocator.fill(applyProbateConfig.page2_phone_num);
    await this.solsAppReferenceLocator.fill(applyProbateConfig.page2_app_ref);

    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async deceasedDetailsPage1(deathTypeDate?: string) {
    await this.verifyPageLoad(this.deceasedForenameLocator);
    await expect(this.deceasedForenameLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.deceasedForenameLocator.fill(deceasedDetailsConfig.page1_forenames);
    await this.deceasedSurnameLocator.fill(deceasedDetailsConfig.page1_surname);
    await this.deceasedDobDayLocator.fill(deceasedDetailsConfig.page1_dateOfBirth_day);
    await this.deceasedDobMonthLocator.fill(deceasedDetailsConfig.page1_dateOfBirth_month);
    await this.deceasedDobYearLocator.fill(deceasedDetailsConfig.page1_dateOfBirth_year);

    if (deathTypeDate === 'EE') {
      await this.deceasedDateOfDeathDayLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_dayEE);
      await this.deceasedDateOfDeathMonthLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_monthEE);
      await this.deceasedDateOfDeathYearLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_yearEE);
    } else {
      await this.deceasedDateOfDeathDayLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_day);
      await this.deceasedDateOfDeathMonthLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_month);
      await this.deceasedDateOfDeathYearLocator.fill(deceasedDetailsConfig.page1_dateOfDeath_year);
    }

    await this.deceasedDomicileLocator.focus();
    await this.deceasedDomicileLocator.click();
    await this.postcodeLinkLocator.focus();
    await this.postcodeLinkLocator.click();
    await expect(this.deceasedAddressLine1Locator).toBeVisible();
    await this.deceasedAddressLine1Locator.fill(deceasedDetailsConfig.address_line1);
    await this.deceasedAddressLine2Locator.fill(deceasedDetailsConfig.address_line2);
    await this.deceasedAddressLine3Locator.fill(deceasedDetailsConfig.address_line3);
    await this.deceasedAddressTownLocator.fill(deceasedDetailsConfig.address_town);
    await this.deceasedAddressCountyLocator.fill(deceasedDetailsConfig.address_county);
    await this.deceasedAddressPostCodeLocator.fill(deceasedDetailsConfig.address_postcode);
    await this.deceasedAddressCountryLocator.fill(deceasedDetailsConfig.address_country);
    await this.deceasedNoAliasNamesLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async deceasedDetailsPage2(applicationType?: string, iHTFormsCompleted?: string, whichIHTFormsCompleted?: string) {
    await this.runAccessibilityTest();

    if (applicationType === 'EE') {
      if (iHTFormsCompleted === 'Yes') {
        await this.ihtFormEstateValueCompleted.click();
        await expect(this.ihtFormsLabelLocator).toBeVisible();
        await expect(this.iht207Locator).toBeVisible();
        await expect(this.iht400Locator).toBeVisible();

        if (whichIHTFormsCompleted === 'IHT207') {
          await this.iht207OptionLocator.click();
        } else if (whichIHTFormsCompleted === 'IHT400') {
          await this.iht400OptionLocator.click();
        } else {
          await this.iht400421OptionLocator.click();
        }

      } else {
        await this.ihtFormEstateValueNotCompleted.click();
        await expect(this.ihtGrossValueLabelLocator).toBeVisible();
        await expect(this.ihtNetValueLabelLocator).toBeVisible();
        await expect(this.ihtNetQualifyingValueLabelLocator).toBeVisible();
        await this.ihtEstateGrossValueLocator.fill(deceasedDetailsConfig.page2_grossValueIHTEstate);
        await this.ihtEstateNetValueLocator.fill(deceasedDetailsConfig.page2_netValueIHTEstate);
        await this.ihtEstateNetQualifyingValueLocator.fill(deceasedDetailsConfig.page2_netQualifyingValueIHTEstate);
        await this.waitForNavigationToComplete(commonConfig.continueButton);

        await this.deceasedLateSpouseLocator.click();
        await expect(this.ihtUnusedAllowanceLocator).toBeVisible();
        await this.ihtUnusedAllowanceLocator.focus();
        await this.ihtUnusedAllowanceLocator.click();
      }
    } else if (applicationType === 'MultiExec') {
      await this.formIdMultiLocator.click();
      await expect(this.nilBandRateLocator).toBeVisible();
      await this.iht217OptionLocator.click();
    } else {
      await this.formIdLocator.click();
    }

    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async provideIhtValues(ihtGrossValue?: string, ihtNetValue?: string, whichIHTFormsCompleted?: string) {
    await this.runAccessibilityTest();
    await expect(this.ihtGrossValueLocator).toBeVisible();
    await this.ihtGrossValueLocator.fill(ihtGrossValue);

    if (whichIHTFormsCompleted === 'IHT400') {
      await expect(this.ihtFormNetValueLocator).toBeVisible();
      await this.ihtFormNetValueLocator.fill(ihtNetValue);
    } else {
      await expect(this.ihtNetValueLocator).toBeVisible();
      await this.ihtNetValueLocator.fill(ihtNetValue);
    }

    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async deceasedDetailsPage3(willType = 'WillLeft') {
    await expect(this.solsWillTypeLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator(`#solsWillType-${willType}`).click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async deceasedDetailsPage4() {
    await expect(this.willDisposeLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.willDisposeOptionLocator.click();
    await this.englishWillLocator.click();
    await this.appointExecLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage1() {
    await this.verifyPageLoad(this.willHasCodicilsLocator);
    await expect(this.willHasCodicilsLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.willAccessOriginalOptionNoLocator.click();
    await expect(this.noWillAccessOriginalLabelLocator).toBeVisible();
    await this.willAccessOriginalOptionYesLocator.click();
    await this.originalWillSignedDayLocator.fill(grantOfProbateConfig.page1_originalWillSignedDate_day);
    await this.originalWillSignedMonthLocator.fill(grantOfProbateConfig.page1_originalWillSignedDate_month);
    await this.originalWillSignedYearLocator.fill(grantOfProbateConfig.page1_originalWillSignedDate_year);
    await this.willHasCodicilsLocator.focus();
    await this.willHasCodicilsLocator.click();
    await expect(this.codicilAddButtonLocator).toBeVisible();
    await this.codicilAddButtonLocator.scrollIntoViewIfNeeded();
    await this.codicilAddButtonLocator.click();

    // exui bug - generating multiple elements with same id
    await this.codicilAddedDayLocator.fill(grantOfProbateConfig.page1_codicilDate_day);
    await this.codicilAddedMonthLocator.fill(grantOfProbateConfig.page1_codicilDate_month);
    await this.codicilAddedYearLocator.fill(grantOfProbateConfig.page1_codicilDate_year);
    await expect(this.languagePreferenceLabelLocator).toBeVisible();
    await this.languagePreferenceWelshLocator.click();
    // await this.page.waitForTimeout(testConfig.ManualDelayLong);
    const isLanguagePreferenceSelected = await this.languagePreferenceWelshLocator.isChecked();
    if (isLanguagePreferenceSelected) {
      await this.waitForNavigationToComplete(commonConfig.continueButton);
    } else {
      await this.languagePreferenceWelshLocator.click();
      await this.waitForNavigationToComplete(commonConfig.continueButton);
    }

  }

  async grantOfProbatePage2(verifyTrustCorpOpts, isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
    await this.runAccessibilityTest();
    // await this.page.waitForTimeout(testConfig.ManualDelayLong);
    if (isSolicitorNamedExecutor || isSolicitorApplyingExecutor) {
      // await this.verifyPageLoad(this.page.getByText(grantOfProbateConfig.page2_prev_identified_execs_text));
      await expect(this.page.getByText(grantOfProbateConfig.page2_prev_identified_execs_text)).toBeVisible();
      await expect(this.page.getByText(grantOfProbateConfig.page2_sol_name)).toBeVisible();
    } else {
      await expect(this.page.getByText(grantOfProbateConfig.page2_prev_identified_execs_text)).not.toBeVisible();
    }
    await this.dispNoticeLocator.scrollIntoViewIfNeeded();
    await expect(this.dispNoticeLocator).toBeVisible();
    await this.dispNoticeLocator.click();
    await expect(this.tctTypeLocator).toBeVisible();
    if (verifyTrustCorpOpts) {
      await this.verifyTitleAndClearingTypeOptionsPage();
    }

    await this.tctTypeLocator.focus();
    await this.tctTypeLocator.click();
    await expect(this.tctTrustCorpLocator).toBeVisible();
    await this.tctTrustCorpLocator.click();
    await expect(this.othersRenouncingLocator).toBeVisible();
    await this.othersRenouncingLocator.click();
    await this.additionalApplyingPartnersLocator.focus();
    await this.additionalApplyingPartnersLocator.click();
    await expect(this.additionalExecutorsLocator).toBeVisible();
    await this.noAdditionalPartnersLocator.click();
    await expect(this.additionalExecutorsLocator).not.toBeVisible();
    await this.tctTrustCorpLocator.focus();
    await this.tctTrustCorpLocator.click();
    await expect(this.trusCorpNameLocator).toBeVisible();
    await this.trusCorpNameLocator.fill(grantOfProbateConfig.page2_nameOfTrustCorp);
    await this.trustCorpPostcodeLinkLocator.click()
    await this.trustCorpAddressLine1Locator.fill(grantOfProbateConfig.address_line1);
    await this.trustCorpAddressLine2Locator.fill(grantOfProbateConfig.address_line2);
    await this.trustCorpAddressLine3Locator.fill(grantOfProbateConfig.address_line3);
    await this.trustCorpPostTownLocator.fill(grantOfProbateConfig.address_town)
    await this.trustCorpCountyLocator.fill(grantOfProbateConfig.address_county);
    await this.trustCorpPostcodeLocator.fill(grantOfProbateConfig.address_postcode);
    await this.trustCorpCountryLocator.fill(grantOfProbateConfig.address_country);

    await expect(this.anyOtherPartnersTextLocator).toBeVisible();
    await this.anyOtherApplyingPartnersTcLocator.focus();
    await this.anyOtherApplyingPartnersTcLocator.click();
    await expect(this.addPersonLocator).toBeVisible();
    await this.addExecutorsTcLocator.click();
    await this.addExecutorFirstnameLocator.fill(grantOfProbateConfig.page2_executorFirstName);
    await this.addExecutorLastnameLocator.fill(grantOfProbateConfig.page2_executorSurname);
    await this.addExecutorTcPositionLocator.fill(grantOfProbateConfig.page2_positionInTrustCorp)
    await this.probatePractitionerPositionLocator.fill(grantOfProbateConfig.page2_positionInTrust);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage2NoExecutors() {
    await this.dispNoticeLocator.scrollIntoViewIfNeeded();
    await expect(this.dispNoticeLocator).toBeVisible();
    await this.dispNoticeLocator.click();
    await expect(this.tctTypeLocator).toBeVisible();
    await this.tctTypeLocator.focus();
    await this.tctTypeLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async verifyTitleAndClearingTypeOptionsPage() {
    const opts = ['TCTPartSuccPowerRes', 'TCTPartPowerRes', 'TCTSolePrinSucc', 'TCTSolePrin', 'TCTPartSuccAllRenouncing',
      'TCTPartAllRenouncing', 'TCTTrustCorpResWithSDJ', 'TCTTrustCorpResWithApp', 'TCTPartSuccOthersRenouncing', 'TCTPartOthersRenouncing', 'TCTNoT'];
      for (let i = 0; i < opts.length; i++) {
      // eslint-disable-next-line no-await-in-loop
      await this.verifyTitleAndClearingTypeOptionPage(opts[i]);
    }
  }

  async verifyTitleAndClearingTypeOptionPage(optName) {
    // await this.verifyPageLoad(this.page.locator(`#titleAndClearingType-${optName}`));
    await expect(this.page.locator(`#titleAndClearingType-${optName}`)).toBeEnabled();
    await this.page.locator(`#titleAndClearingType-${optName}`).scrollIntoViewIfNeeded();
    await this.page.locator(`#titleAndClearingType-${optName}`).click();

    const isNa = optName === 'TCTNoT';
    const isTrustOption = optName.startsWith('TCTTrustCorp');
    const allRenouncing = optName.endsWith('AllRenouncing');
    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing' || optName === 'TCTPartSuccOthersRenouncing';

    if (!isNa && !allRenouncing && !isTrustOption && isSuccessorFirm) {
      await expect(this.firmNameWillTextLocator).toBeVisible();
      await this.firmNameInWillLocator.scrollIntoViewIfNeeded();
    }
  }

  async grantOfProbatePage3() {
    // await this.verifyPageLoad(this.displenseWithNoticeLeaveLocator);
    await expect(this.displenseWithNoticeLeaveLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.displenseWithNoticeLeaveLocator.click();
    await this.dispenseWithNoticeOverviewLocator.fill(grantOfProbateConfig.page3_dispenseWithNoticeOverview);
    await this.dispenseWithNoticeSupportingDocsLocator.fill(grantOfProbateConfig.page3_dispenseWithNoticeSupportingDocs);
    await this.dispenseWithNoticeOtherExecsListLocator.click();
    await this.dispenseWithNoticeExecutorNotApplyingName.fill(grantOfProbateConfig.page3_dispenseWithNoticeName);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage4ExecNotApplying() {
    await expect(this.primaryApplicantForenameLocator).toBeEnabled();
    await this.primaryApplicantForenameLocator.fill(grantOfProbateConfig.page2_sol_forename);
    await expect(this.primaryApplicantSurnameLocator).toBeEnabled();
    await this.primaryApplicantSurnameLocator.fill(grantOfProbateConfig.page2_sol_surname);
    await expect(this.page.locator('#primaryApplicantIsApplying_No')).toBeEnabled();
    await this.page.locator('#primaryApplicantIsApplying_No').click();
    await expect(this.page.locator('#solsPrimaryExecutorNotApplyingReason-Renunciation')).toBeEnabled();
    await this.page.locator('#solsPrimaryExecutorNotApplyingReason-Renunciation').click();
    await expect(this.page.locator('#otherExecutorExists_No')).toBeEnabled();
    await this.page.locator('#otherExecutorExists_No').click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage4(isSolicitorApplying = false) {
    // await this.verifyPageLoad(this.otherExecutorExistsLocator);
    await expect(this.otherExecutorExistsLocator).toBeEnabled();
    await this.runAccessibilityTest();

    if (isSolicitorApplying) {
      await this.otherExecutorExistsValueLocator.click();
      await expect(this.additionalExecutorTextLocator).toBeVisible();
      await expect(this.prevIdentifiedApplyingExecutors).toBeVisible();
      await expect(this.prevIdentifiedNotApplyingEexecutors).toBeVisible();
      await this.solsAddExecutorsList.click();
      await this.solsAddExectorsForenameLocator.fill(grantOfProbateConfig.page2_executorFirstName);
      await this.solsAddExecutorLastnameLocator.fill(grantOfProbateConfig.page2_executorSurname);
      await this.solsAddExecutorNameOnWillOptionLocator.click();
      await expect(this.solsAddExecutorNameOnWillLocator).toBeVisible();
      await this.solsAddExecutorNameOnWillLocator.fill(grantOfProbateConfig.page2_executorAliasName);
      await this.solsAddExecutorsApplying.click();
      await expect(this.solsAddExecutorApplyingPostcodeLocator).toBeEnabled();
      await this.solsAddExecutorApplyingPostcodeLocator.fill(grantOfProbateConfig.page2_executorPostcode);
      await this.solsAddExecutorFindAddressLocator.click();
      await expect(this.solsAddExecutorAddressListLocator).toBeEnabled();
      await expect(this.solsAddExecutorAddressOptionLocator).toBeEnabled();

      const optText = await this.solsAddExecutorAddressOptionLocator.textContent();
      if (optText && optText.indexOf(grantOfProbateConfig.noAddressFound) >= 0) {
        await expect(this.addExecAddressLocator).toBeVisible();
        await this.addExecAddressLocator.focus();
        await this.addExecAddressLocator.click();
        await expect(this.addExecutorAddressLine1Locator).toBeVisible();
        await this.addExecutorAddressLine1Locator.fill(grantOfProbateConfig.page2_executorAddress_line1);
        await this.addExecutorAddressLine2Locator.fill(grantOfProbateConfig.page2_executorAddress_line2);
        await this.addExecutorAddressLine3Locator.fill(grantOfProbateConfig.page2_executorAddress_line3);
        await this.addExecutorPostTownLocator.fill(grantOfProbateConfig.page2_executorAddress_town);
        await this.addExecutorPostCodeLocator.fill(grantOfProbateConfig.page2_executorPostcode);
        await this.addExecutorCountryLocator.fill(grantOfProbateConfig.page2_executorAddress_country);
      } else {
        await this.solsAddExecutorAddressListLocator.click();
      }

    } else {
      await this.otherExecutorNotExistsLocator.click();
    }
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage5() {
    // await this.verifyPageLoad(this.furtherEvidenceLocator);
    await expect(this.furtherEvidenceLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.furtherEvidenceLocator.fill(grantOfProbateConfig.page5_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async grantOfProbatePage6() {
    // await this.verifyPageLoad(this.additionalInfoLocator);
    await expect(this.additionalInfoLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.additionalInfoLocator.fill(grantOfProbateConfig.page5_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async completeApplicationUpdateSot() {
    await this.verifyPageLoad(this.sotUpdateRequiredYesLocator);
    await expect(this.sotUpdateRequiredYesLocator).toBeVisible();
    await this.sotUpdateRequiredYesLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async selectReenterTask(task) {
    await this.verifyPageLoad(this.page.locator('#solsAmendLegalStatmentSelect'));
    await expect(this.page.locator('#solsAmendLegalStatmentSelect')).toBeEnabled();
    await this.page.locator('#solsAmendLegalStatmentSelect').selectOption(task);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async completeApplicationPage1(willType = 'WillLeft') {
    await this.verifyPageLoad(this.sotUpdateRequiredLocator);
    await expect(this.sotUpdateRequiredLocator).toBeVisible();
    await expect(this.reviewLegalStatement1Locator).toBeVisible();
    await expect(this.reviewLegalStatement2Locator).toBeVisible();
    await expect(this.reviewLegalStatement3Locator).toBeVisible();
    await expect(this.reviewLegalStatement4Locator).toBeVisible();
    await this.runAccessibilityTest();
    if (willType === 'WillLeftAnnexed') {
      await expect(this.admonWillLegalStatementLink).toBeVisible();
    } else if (willType === 'NoWill') {
      await expect(this.intestacyLegalStatementLink).toBeVisible();
    } else {
      await expect(this.legalStatementLink).toBeVisible();
    }

    await this.sotUpdateNotRequiredLocator.click();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async completeApplicationPage3() {
    await this.verifyPageLoad(this.completeApplicationWaitForText);
    await expect(this.completeApplicationWaitForText).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async completeApplicationPage4() {
    await this.verifyPageLoad(this.sotConfirmCheck1Locator);
    await this.runAccessibilityTest();
    await this.sotConfirmCheck1Locator.scrollIntoViewIfNeeded();
    await this.sotConfirmCheck1Locator.click();
    await this.sotConfirmCheck2Locator.click();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async completeApplicationPage5() {
    await this.verifyPageLoad(this.extrCopiesLocator);
    await expect(this.extrCopiesLocator).toBeVisible();

    /*****Need to uncomment this accessibility test after fixing the bug in exui ******/
    // await this.runAccessibilityTest();
    await this.extrCopiesLocator.fill(completeProbateApplicationConfig.page5_extraCopiesUK);
    await this.extraCopiesOutsideUKLocator.fill(completeProbateApplicationConfig.page5_outsideUKGrantCopies);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async completeApplicationPage6() {
    await this.verifyPageLoad(this.solsPbaPaymentRefLocator);
    await expect(this.solsPbaPaymentRefLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.solsPbaPaymentRefLocator.fill(completeProbateApplicationConfig.page6_paymentReference);
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async completeApplicationPage7() {
    await this.verifyPageLoad(this.page.getByText(completeProbateApplicationConfig.page7_waitForText));
    await expect(this.page.getByText(completeProbateApplicationConfig.page7_waitForText)).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async completeApplicationPage8() {
    await this.verifyPageLoad(this.page.getByText(completeProbateApplicationConfig.page8_waitForText));
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_waitForText)).toBeVisible();
    await this.runAccessibilityTest();
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_applicationFee)).toBeVisible();
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_additionalCopiesFee)).toBeVisible();
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_feeForCertifiedCopies)).toBeVisible();
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_totalFeeAmount)).toBeVisible();
    await expect(this.page.getByText(completeProbateApplicationConfig.page8_customerReference)).toBeVisible();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async makePaymentPage1(caseRef, serviceRequestTabConfig) {
    await this.verifyPageLoad(this.page.getByRole('heading', { name: caseRef }));
    await expect(this.page.getByRole('heading', { name: caseRef })).toBeVisible();
    await expect(this.page.getByRole('link', { name: makePaymentConfig.paymentLinkText })).toBeVisible();
    await this.page.getByRole('link', { name: makePaymentConfig.paymentLinkText }).click();
    await this.page.waitForLoadState('domcontentloaded');
    await expect(this.serviceRequestTab).toBeEnabled();
    await this.serviceRequestTab.focus();
    await this.serviceRequestTab.click();
    await this.runAccessibilityTest();
    for (let i = 0; i < serviceRequestTabConfig.fields.length; i++) {
      if (serviceRequestTabConfig.fields[i] && serviceRequestTabConfig.fields[i] !== '') {
        await expect(this.page.getByText(serviceRequestTabConfig.fields[i]).first()).toBeVisible(); // eslint-disable-line no-await-in-loop
      }
    }

    await this.verifyPageLoad(this.reviewLocator);
    await expect(this.reviewLocator).toBeVisible();
    await this.reviewLocator.click();
  }

  async intestacyDetailsPage1() {
    await this.verifyPageLoad(this.primaryApplicantForenameLocator);
    await expect(this.primaryApplicantForenameLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.primaryApplicantForenameLocator.fill(intestacyDetailsConfig.applicant_firstname);
    await this.primaryApplicantSurnameLocator.fill(intestacyDetailsConfig.applicant_lastname);
    await this.page.locator(intestacyDetailsConfig.UKpostcodeLink).click();
    await this.primaryApplicantAddressLine1.fill(intestacyDetailsConfig.address_line1);
    await this.primaryApplicantAddressLine2.fill(intestacyDetailsConfig.address_line2);
    await this.primaryApplicantAddressLine3.fill(intestacyDetailsConfig.address_line3);
    await this.primaryApplicantPostTown.fill(intestacyDetailsConfig.address_town);
    await this.primaryApplicantCounty.fill(intestacyDetailsConfig.address_county);
    await this.primaryApplicantPostcode.fill(intestacyDetailsConfig.address_postcode);
    await this.primaryApplicantCountry.fill(intestacyDetailsConfig.address_country);
    await this.primaryApplicantPhoneNumber.fill(intestacyDetailsConfig.applicant_phone);
    await this.primaryApplicantEmail.fill(intestacyDetailsConfig.applicant_email);
    await this.languageLocator.focus();
    await this.languageLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async intestacyDetailsPage2() {
    // await this.verifyPageLoad(this.page.locator('#solsMinorityInterest'));
    await expect(this.page.locator('#solsMinorityInterest')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator(`#solsApplicantRelationshipToDeceased-${intestacyDetailsConfig.page2_child}`).click();
    await this.page.locator(`#solsApplicantSiblings_${intestacyDetailsConfig.optionNo}`).click();
    await this.page.locator(`#deceasedMaritalStatus-${intestacyDetailsConfig.page2_maritalstatus}`).click();
    await this.page.locator(`#solsMinorityInterest_${intestacyDetailsConfig.optionNo}`).click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async intestacyDetailsPage3() {
    // await this.verifyPageLoad(this.page.locator('#furtherEvidenceForApplication'));
    await expect(this.page.locator('#furtherEvidenceForApplication')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator('#furtherEvidenceForApplication').fill(intestacyDetailsConfig.page3_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async intestacyDetailsPage4() {
    // await this.verifyPageLoad(this.page.locator('#solsAdditionalInfo'));
    await expect(this.page.locator('#solsAdditionalInfo')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator('#solsAdditionalInfo').fill(intestacyDetailsConfig.page3_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async enterIhtDetails(caseProgressConfig, optionValue) {
    // await this.verifyPageLoad(this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`));
    await expect(this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`)).toBeEnabled();
    await this.page.locator(`${caseProgressConfig.ihtHmrcLetter}_${optionValue}`).click();
    // await I.click({css: `${caseProgressConfig.ihtHmrcLetter}_${optionValue}`});
    if (optionValue === 'Yes') {
      await expect(this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`)).toBeEnabled();
      await this.page.locator(`${caseProgressConfig.hmrcCodeTextBox}`).fill(caseProgressConfig.uniqueHmrcCode);
    }
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async caseProgressHmrcStopPage(caseProgressConfig) {
    // await this.verifyPageLoad(this.page.getByText(caseProgressConfig.ihtHmrcLetterNotReceived));
    await expect(this.page.getByText(caseProgressConfig.ihtHmrcLetterNotReceived)).toBeVisible();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage1() {
    await this.verifyPageLoad(this.willAccessOriginalOptionNoLocator);
    await expect(this.willAccessOriginalOptionNoLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.willAccessOriginalOptionNoLocator.click();
    await expect(this.noWillAccessOriginalLabelLocator).toBeVisible();
    await this.willAccessOriginalOptionYesLocator.click();
    await this.originalWillSignedDayLocator.fill(admonWillDetailsConfig.page1_originalWillSignedDate_day);
    await this.originalWillSignedMonthLocator.fill(admonWillDetailsConfig.page1_originalWillSignedDate_month);
    await this.originalWillSignedYearLocator.fill(admonWillDetailsConfig.page1_originalWillSignedDate_year);
    await this.willAccessOriginalOptionYesLocator.click();
    await this.willHasCodicilsLocator.click();
    await expect(this.codicilAddButtonLocator).toBeVisible();
    await expect(this.codicilAddButtonLocator).toBeEnabled();
    await this.codicilAddButtonLocator.click();
    await this.codicilAddedDayLocator.fill(admonWillDetailsConfig.page1_codicilDate_day);
    await this.codicilAddedMonthLocator.fill(admonWillDetailsConfig.page1_codicilDate_month);
    await this.codicilAddedYearLocator.fill(admonWillDetailsConfig.page1_codicilDate_year);
    await this.languageLocator.click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage2(updateAddressManually) {
    // await this.verifyPageLoad(this.primaryApplicantForenameLocator);
    await expect(this.primaryApplicantForenameLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.primaryApplicantForenameLocator.fill(admonWillDetailsConfig.applicant_firstname);
    await this.primaryApplicantSurnameLocator.fill(admonWillDetailsConfig.applicant_lastname);

    if (updateAddressManually) {
      await this.page.locator(admonWillDetailsConfig.UKpostcodeLink).click();
    }

    await this.primaryApplicantAddressLine1.fill(admonWillDetailsConfig.address_line1);
    await this.primaryApplicantAddressLine2.fill(admonWillDetailsConfig.address_line2);
    await this.primaryApplicantAddressLine3.fill(admonWillDetailsConfig.address_line3);
    await this.primaryApplicantPostTown.fill(admonWillDetailsConfig.address_town);
    await this.primaryApplicantCounty.fill(admonWillDetailsConfig.address_county);
    await this.primaryApplicantPostcode.fill(admonWillDetailsConfig.address_postcode);
    await this.primaryApplicantCountry.fill(admonWillDetailsConfig.address_country);
    await this.primaryApplicantPhoneNumber.fill(admonWillDetailsConfig.applicant_phone);
    await this.primaryApplicantEmail.fill(admonWillDetailsConfig.applicant_email);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage3() {
    // await this.verifyPageLoad(this.solsEntitledMinority);
    await expect(this.solsEntitledMinority).toBeEnabled();
    await this.runAccessibilityTest();
    await this.solsEntitledMinority.click();
    await this.solsDiedLocator.click();
    await this.solsResiduary.click();
    await this.page.selectOption('#solsResiduaryType', admonWillDetailsConfig.page3_legateeAndDevisee);
    await this.page.locator(`#solsLifeInterest_${admonWillDetailsConfig.optionNo}`).click();
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage4() {
    // await this.verifyPageLoad(this.page.locator('#furtherEvidenceForApplication'));
    await expect(this.page.locator('#furtherEvidenceForApplication')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator('#furtherEvidenceForApplication').fill(admonWillDetailsConfig.page4_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage5() {
    // await this.verifyPageLoad(this.page.locator('#solsAdditionalInfo'));
    await expect(this.page.locator('#solsAdditionalInfo')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator('#solsAdditionalInfo').fill(admonWillDetailsConfig.page5_applicationNotes);
    await this.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async admonWillDetailsPage6() {
    // await this.verifyPageLoad(this.page.locator('#confirmation-body'));
    await expect(this.page.locator('#confirmation-body')).toBeEnabled();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
  }

  async verifyNoc(caseRef, caseType) {
    await this.verifyPageLoad(this.page.getByText('Your cases'));
    await expect(this.page.getByText('Your cases')).toBeVisible();
    await this.navigateToCase(caseRef, false, caseType);
    await expect(this.page.getByRole('heading', { name: nocConfig.nocVerifyText })).toBeVisible();
    await expect(this.page.getByText(caseRef)).not.toBeVisible();
  }

  async navigateToCase(caseRef: string, useWaitInUrl?: boolean, caseType?: string) {
    const scenarioName = 'Find cases';
    await this.logInfo(scenarioName, 'Navigating to case');
    const caseRefNoDashes = caseRef.replaceAll('-', '');
    const caveatUrl = `${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/Caveat/${caseRefNoDashes}`;
    const gorUrl = `${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/GrantOfRepresentation/${caseRefNoDashes}`;
    const url = caseType === 'Caveat' ? caveatUrl : gorUrl;
    await this.page.goto(url);

    await this.rejectCookies();
  }

  async nocNavigation() {
    await this.verifyPageLoad(this.caseListHeadingLocator);
    await expect(this.caseListHeadingLocator).toBeVisible();
    await this.rejectCookies();
    await expect(this.page.locator(nocConfig.xuiNocLocator)).toBeEnabled();
    await this.waitForNavigationToComplete(nocConfig.xuiNocLocator)
  }

  async nocPage1(caseRef) {
    await this.verifyPageLoad(this.page.getByRole('heading', { name: nocConfig.page1WaitForText }));
    await expect(this.page.getByRole('heading', { name: nocConfig.page1WaitForText })).toBeVisible();
    await expect(this.page.locator(nocConfig.caseRefLocator)).toBeEnabled();
    await this.page.locator(nocConfig.caseRefLocator).fill(caseRef);
    await this.page.locator(nocConfig.continueButtonLocator).click();
  }

  async nocPage2(deceasedSurname) {
    // await this.verifyPageLoad(this.page.getByText(nocConfig.page2WaitForText));
    await expect(this.page.getByText(nocConfig.page2WaitForText)).toBeVisible();
    await expect(this.page.locator(nocConfig.deceasedSurnameLocator)).toBeEnabled();
    await this.page.locator(nocConfig.deceasedSurnameLocator).fill(deceasedSurname);
    await this.page.locator(nocConfig.continueButtonLocator).click();
  }

  async nocPage3(caseRef, deceasedSurname) {
    // await this.verifyPageLoad(this.page.getByText(nocConfig.page3WaitForText));
    await expect(this.page.getByText(nocConfig.page3WaitForText)).toBeVisible();

    const caseRefNoDashes = await caseRef.replaceAll('-', '');
    await expect(this.page.getByText(caseRefNoDashes)).toBeVisible();
    await expect(this.page.getByText(deceasedSurname)).toBeVisible();
    await expect(this.page.locator(nocConfig.affirmationLocator)).toBeEnabled();
    await this.page.locator('#affirmation').click();
    await expect(this.page.locator(nocConfig.notifyCheckboxLocator)).toBeEnabled();
    await this.page.locator('#notifyEveryParty').click();
    await this.page.locator(nocConfig.continueButtonLocator).click();
  }

  async nocConfirmationPage(caseRef) {
    // await this.verifyPageLoad(this.page.getByText(nocConfig.confirmationPageWaitForText));
    await expect(this.page.getByText(nocConfig.confirmationPageWaitForText)).toBeVisible();
    await expect(this.page.getByText(caseRef)).toBeVisible();
    await expect(this.page.locator(nocConfig.viewCaseLinkLocator)).toBeEnabled();
    await this.waitForNavigationToComplete(nocConfig.viewCaseLinkLocator);
  }

  async shareCaseSelection(sacCaseRefNumber) {
    await this.verifyPageLoad(this.caseListLocator);
    await expect(this.caseListLocator).toBeVisible();
    await this.caseListLocator.click();
    await expect(this.caseViewTextLocator).toBeVisible();
    await this.page.locator('#wb-jurisdiction').selectOption(shareCaseConfig.case_Jurisdiction);
    await this.page.locator('#wb-case-type').selectOption(shareCaseConfig.caseType);
    await this.page.locator('//button[normalize-space()="Apply"]').click();
    await this.caseReferenceLocator.click();
    await this.page.getByLabel(shareCaseConfig.caseList_sortCase).click();
    await expect(this.page.locator('//input[@id="select-' + sacCaseRefNumber + '"]')).toBeEnabled();
    await this.page.locator('//input[@id="select-' + sacCaseRefNumber + '"]').click();
    await this.shareCaseButtonLocator.click();
    await this.page.waitForLoadState();
    // await this.page.waitForTimeout(10);
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).focus();
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).click();
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).focus();
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).fill('a');
    await this.showAllTextLocator.click();
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).click();
    await this.page.locator(shareCaseConfig.shareCase_comboBoxLocator).fill('Te');
    await this.page.getByRole('option', { name: shareCaseConfig.secondProbatePractitioner_value }).click();
    await expect(this.page.locator('#btn-add-user')).toBeEnabled();
    await this.page.locator('#btn-add-user').click();
    await expect(this.page.getByText(shareCaseConfig.secondProbatePractitioner_email)).toBeVisible();
    await expect(this.page.getByText(shareCaseConfig.caseAdded_Text)).toBeVisible();
    await this.page.getByRole('button', { name: 'Continue' }).click();
    await this.page.getByRole('button', { name: 'Confirm' }).click();
    await expect(this.page.getByText('Your cases have been updated')).toBeVisible();
  }

  async verifyShareCase(sacCaseRefNumber) {
    await this.verifyPageLoad(this.caseViewTextLocator);
    await expect(this.caseViewTextLocator).toBeVisible();
    await this.caseReferenceLocator.click();
    await this.page.getByLabel(shareCaseConfig.caseList_sortCase).click();
    await expect(this.page.locator('//input[@id="select-' + sacCaseRefNumber + '"]')).toBeEnabled();
    await this.page.locator('//input[@id="select-' + sacCaseRefNumber + '"]').click();
    await this.waitForNavigationToComplete('#btn-share-button');
    await this.showAllTextLocator.click();
    await this.page.locator(`//tr[contains(.,"${testConfig.TestEnvProfUser}")]`).getByText('Remove').click();
    await expect(this.page.getByText(shareCaseConfig.caseRemoved_Text)).toBeVisible();
    await this.page.getByRole('button', { name: shareCaseConfig.continueButton }).click();
    await this.page.getByRole('button', { name: shareCaseConfig.confirmButton }).click();
    await expect(this.page.getByText(shareCaseConfig.caseAdded_validationText)).toBeVisible();
  }

  async shareCaseVerifyUserRemove(sacCaseRefNumber) {
    await this.verifyPageLoad(this.caseViewTextLocator);
    await expect(this.caseViewTextLocator).toBeVisible();
    await this.caseReferenceLocator.click();
    await this.page.getByLabel(shareCaseConfig.caseList_sortCase).click();
    await expect(this.page.locator('//input[@id="select-' + sacCaseRefNumber + '"]')).not.toBeVisible();
  }

  async shareCaseDelete(caseIdShareCase, caseRef) {
    await this.verifyPageLoad(this.caseViewTextLocator);
    await expect(this.caseViewTextLocator).toBeVisible();
    const caseRefNoDashes = caseRef.replaceAll('-', '');
    await this.page.goto(`${testConfig.TestBackOfficeUrl}/cases/case-details/PROBATE/GrantOfRepresentation/${caseRefNoDashes}`);
    await this.page.locator('//select[@id="next-step"]').selectOption('Delete');
    await this.waitForNavigationToComplete(this.goButtonLocator);
    await expect(this.page.getByText('#'+caseIdShareCase)).toBeVisible();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
    await expect(this.page.getByText('#'+caseIdShareCase)).toBeVisible();
    await this.waitForNavigationToComplete(commonConfig.submitButton);
    await expect(this.page.getByText('Case #' + caseIdShareCase + ' has been updated with event: Delete')).toBeVisible();
  }

};
