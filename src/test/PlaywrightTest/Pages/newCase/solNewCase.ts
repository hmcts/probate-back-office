import { expect, Page, TestInfo } from "@playwright/test";
import dateFns from "date-fns";
import { testConfig } from "../../Configs/config.ts";
import postPaymentReviewTabConfig from "../caseDetails/solicitorApplyProbate/postPaymentReviewTabConfig.json" with { type: "json" };
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
import { BasePage } from "../utility/basePage.ts";

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
    readonly confirmPaymentButton = this.page.getByRole("button", {name: "Confirm payment",});
    readonly serviceRequestLinkLocator = this.page.getByRole("link", {
      name: makePaymentConfig.serviceRequestLink,
      exact: true,
    });
    readonly eventHistoryTab = this.page.getByRole("tab", {name: makePaymentConfig.eventHistoryTab,});
    readonly caseProgressTabLocator = this.page.getByRole("tab", {
      name: makePaymentConfig.caseProgressTab,
      exact: true,
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
  readonly iht207Locator = this.page.getByText(deceasedDetailsConfig.page2_IHT207Label);
  readonly iht400Locator = this.page.getByText(deceasedDetailsConfig.page2_IHT400Label);
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
  readonly ihtUnusedAllowanceLocator = this.page.locator(`#unusedAllowanceQuestion_${deceasedDetailsConfig.optionYes}`);
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
  readonly codiilAddedYearLocator = this.page.locator('#dateCodicilAdded-year');
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
  readonly additionalExecutorTextLocator = this.page.getByText(grantOfProbateConfig.page2_waitForAdditionalExecutor);
  readonly prevIdentifiedApplyingExecutors = this.page.getByText(grantOfProbateConfig.page4_previouslyIdentifiedApplyingExecutors);
  readonly prevIdentifiedNotApplyingEexecutors = this.page.getByText(grantOfProbateConfig.page4_previouslyIdentifiedNotApplyingExecutors);
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

  constructor(public readonly page: Page) {
    super(page);
  }

  async applyCaveatPage1() {
    await expect(this.page.locator("#solsCaveatEligibility")).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForNavigationToComplete();
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
    await this.waitForNavigationToComplete();
  }

  async cyaPage() {
    await expect(this.page.getByText("Check your answers")).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForSubmitNavigationToComplete("Save and continue");
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
    await this.waitForNavigationToComplete();
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
      await this.page.waitForTimeout(testConfig.ManualDelayShort);
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
        if (!testConfig.TestAutoDelayEnabled) {
          // only valid for local dev where we need it to run as fast as poss to minimise
          // lost dev time
          await this.page.waitForTimeout(testConfig.ManualDelayShort);
        }
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
    await this.waitForNavigationToComplete();
  }

  async completeCaveatApplicationPage1() {
    await this.runAccessibilityTest();
    await this.page
      .locator("#solsPBAPaymentReference")
      .fill(completeApplicationConfig.page1_paymentReference);
    await this.page
      .locator("input#paymentConfirmCheckbox-paymentAcknowledgement")
      .click();
    await this.waitForSubmitNavigationToComplete(commonConfig.continueButton);
  }

  async completeCaveatApplicationPage2(caseRef: string) {
    completeApplicationConfig.page2_notification_date = dateFns.format(
      new Date(),
      testConfig.dateFormat
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
  }

  async viewPaymentStatus(testInfo: TestInfo, caseRef: string, appType: string) {
    await expect(this.page.getByText(caseRef).first()).toBeVisible();
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
      await this.page.waitForTimeout(10);
      if (result) {
        break;
      }
      await this.page.reload();
      // await I.amOnLoadedPage(`${testConfig.TestBackOfficeUrl}/cases/case-details/${caseRefNoDashes}`);
    }
    if (appType !== "Caveat") {
      await expect(this.caseProgressTabLocator).toBeEnabled();
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
    await expect(this.solsStartPageLocator).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForSubmitNavigationToComplete("Save and continue");
  }

  async applyForProbatePage2(isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
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

    await this.waitForNavigationToComplete();
  }

  async deceasedDetailsPage1(deathTypeDate) {
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
    await this.waitForNavigationToComplete();
  }

  async deceasedDetailsPage2(applicationType, iHTFormsCompleted, whichIHTFormsCompleted) {
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
        await this.waitForNavigationToComplete();

        await this.deceasedLateSpouseLocator.click();
        await this.ihtUnusedAllowanceLocator.click();
      }
    } else if (applicationType === 'MultiExec') {
      await this.formIdMultiLocator.click();
      await expect(this.nilBandRateLocator).toBeVisible();
      await this.iht217OptionLocator.click();
    } else {
      await this.formIdLocator.click();
    }

    await this.waitForNavigationToComplete();
  }

  async provideIhtValues(ihtGrossValue, ihtNetValue, whichIHTFormsCompleted) {
    await this.runAccessibilityTest();
    await expect(this.ihtGrossValueLocator).toBeVisible();
    await this.ihtGrossValueLocator.fill(ihtGrossValue);

    // await I.waitForElement({css: '#ihtGrossValue'});
    // await I.fillField({css: '#ihtGrossValue'}, ihtGrossValue);
    if (whichIHTFormsCompleted === 'IHT400') {
      await expect(this.ihtFormNetValueLocator).toBeVisible();
      await this.ihtFormNetValueLocator.fill(ihtNetValue);

      // await I.waitForElement({css: '#ihtFormNetValue'});
      // await I.fillField({css: '#ihtFormNetValue'}, ihtNetValue);
    } else {
      await expect(this.ihtNetValueLocator).toBeVisible();
      await this.ihtNetValueLocator.fill(ihtNetValue);

      // await I.waitForElement({css: '#ihtNetValue'});
      // await I.fillField({css: '#ihtNetValue'}, ihtNetValue);
    }

    await this.waitForNavigationToComplete();
    // await I.waitForNavigationToComplete(commonConfig.continueButton);
  }

  async deceasedDetailsPage3(willType = 'WillLeft') {
    await expect(this.solsWillTypeLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.page.locator(`#solsWillType-${willType}`).click();
    await this.waitForNavigationToComplete();

    // await I.waitForElement('#solsWillType');
    // await I.runAccessibilityTest();
    // await I.click(`#solsWillType-${willType}`);
    //
    // await I.waitForNavigationToComplete(commonConfig.continueButton, true);
  }

  async deceasedDetailsPage4() {
    await expect(this.willDisposeLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.willDisposeOptionLocator.click();
    await this.englishWillLocator.click();
    await this.appointExecLocator.click();
    await this.waitForNavigationToComplete();

    // await I.waitForElement('#willDispose');
    // await I.runAccessibilityTest();
    //
    // await I.click(`#willDispose_${deceasedDetailsConfig.optionYes}`);
    // await I.click(`#englishWill_${deceasedDetailsConfig.optionYes}`);
    // await I.click(`#appointExec_${deceasedDetailsConfig.optionYes}`);
    //
    // await I.waitForNavigationToComplete(commonConfig.continueButton, true);
  }

  async grantOfProbatePage1() {
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
    await this.codiilAddedYearLocator.fill(grantOfProbateConfig.page1_codicilDate_year);
    await expect(this.languagePreferenceLabelLocator).toBeVisible();
    await this.languagePreferenceWelshLocator.click();
    await this.waitForNavigationToComplete();

    // await I.waitForElement(`#willHasCodicils_${grantOfProbateConfig.optionYes}`);
    // await I.runAccessibilityTest();

    // await I.click({css: '#willAccessOriginal_No'});
    // await I.waitForText(grantOfProbateConfig.page1_noAccessOriginalWillLabel);

    // await I.click({css: `#willAccessOriginal_${grantOfProbateConfig.optionYes}`});

    // await I.fillField({css: '#originalWillSignedDate-day'}, grantOfProbateConfig.page1_originalWillSignedDate_day);
    // await I.fillField({css: '#originalWillSignedDate-month'}, grantOfProbateConfig.page1_originalWillSignedDate_month);
    // await I.fillField({css: '#originalWillSignedDate-year'}, grantOfProbateConfig.page1_originalWillSignedDate_year);

    // await I.click({css: `#willHasCodicils_${grantOfProbateConfig.optionYes}`});
    // const addBtn = {css: '#codicilAddedDateList button'};
    // await I.waitForVisible(addBtn);
    // await I.scrollTo(addBtn);
    // await I.waitForClickable(addBtn);
    // await I.click(addBtn);
    /*if (!testConfig.TestAutoDelayEnabled) {
      await I.wait(testConfig.ManualDelayShort);
    }*/


    // await I.fillField({css: '#dateCodicilAdded-day'}, grantOfProbateConfig.page1_codicilDate_day);
    // await I.fillField({css: '#dateCodicilAdded-month'}, grantOfProbateConfig.page1_codicilDate_month);
    // await I.fillField({css: '#dateCodicilAdded-year'}, grantOfProbateConfig.page1_codicilDate_year);
    // await I.waitForText(grantOfProbateConfig.page1_languagePreferenceLabel);
    // await I.click({css: `#languagePreferenceWelsh_${grantOfProbateConfig.optionYes}`});

    // await I.waitForNavigationToComplete(commonConfig.continueButton, true);
  }

  async grantOfProbatePage2(verifyTrustCorpOpts, isSolicitorNamedExecutor = false, isSolicitorApplyingExecutor = false) {
    await this.runAccessibilityTest();

    // const dispNoticeLocator = {css: `#dispenseWithNotice_${grantOfProbateConfig.page2_dispenseWithNotice}`};
    /*if (!testConfig.TestAutoDelayEnabled) {
      await I.wait(testConfig.ManualDelayMedium);
    }*/

    if (isSolicitorNamedExecutor || isSolicitorApplyingExecutor) {
      await expect(this.page.getByText(grantOfProbateConfig.page2_prev_identified_execs_text)).toBeVisible();
      await expect(this.page.getByText(grantOfProbateConfig.page2_sol_name)).toBeVisible();
      // await I.waitForText(grantOfProbateConfig.page2_prev_identified_execs_text);
      // await I.waitForText(grantOfProbateConfig.page2_sol_name);
    } else {
      await expect(this.page.getByText(grantOfProbateConfig.page2_prev_identified_execs_text)).not.toBeVisible();
      // await I.dontSee(grantOfProbateConfig.page2_prev_identified_execs_text);
    }
    await this.dispNoticeLocator.scrollIntoViewIfNeeded();
    await expect(this.dispNoticeLocator).toBeVisible();
    await this.dispNoticeLocator.click();
    await expect(this.tctTypeLocator).toBeVisible();
    // await I.scrollTo(dispNoticeLocator);
    // await I.waitForClickable(dispNoticeLocator);
    // await I.click(dispNoticeLocator);
    // await I.waitForElement('#titleAndClearingType-TCTNoT', 40);
    if (verifyTrustCorpOpts) {
      await this.verifyTitleAndClearingTypeOptionsPage();
    }
    // else {
    //   // await I.wait(2);
    //   await this.tctTypeLocator.scrollIntoViewIfNeeded();
    //   // await I.scrollTo({css: '#titleAndClearingType-TCTNoT'});
    // }

    await this.tctTypeLocator.focus();
    await this.tctTypeLocator.click();
    await expect(this.tctTrustCorpLocator).toBeVisible();
    await this.tctTrustCorpLocator.click();
    await expect(this.othersRenouncingLocator).toBeVisible();
    await this.othersRenouncingLocator.click();
    // await this.additionalApplyingPartnersLocator.scrollIntoViewIfNeeded();
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
    await this.waitForNavigationToComplete();
    // await I.waitForClickable({css: '#titleAndClearingType-TCTNoT'});
    // await I.click({css: '#titleAndClearingType-TCTNoT'});

    // await I.waitForClickable({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    // await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    // await I.click({css: '#titleAndClearingType-TCTPartOthersRenouncing'});

    // await I.scrollTo({css: '#anyOtherApplyingPartners_Yes'});
    // await I.click({css: '#anyOtherApplyingPartners_Yes'});
    // await I.waitForVisible({css: '#otherPartnersApplyingAsExecutors'});
    // await I.click({css: '#anyOtherApplyingPartners_No'});
    // await I.waitForInvisible({css: '#otherPartnersApplyingAsExecutors'});

    // await I.scrollTo({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});
    // await I.click({css: '#titleAndClearingType-TCTTrustCorpResWithApp'});

    // await I.waitForElement('#trustCorpName');
    // await I.fillField('#trustCorpName', grantOfProbateConfig.page2_nameOfTrustCorp);
    // await I.click(grantOfProbateConfig.page2_trustCorpPostcodeLink);

    // await I.fillField('#trustCorpAddress__detailAddressLine1', grantOfProbateConfig.address_line1);
    // await I.fillField('#trustCorpAddress__detailAddressLine2', grantOfProbateConfig.address_line2);
    // await I.fillField('#trustCorpAddress__detailAddressLine3', grantOfProbateConfig.address_line3);
    // await I.fillField('#trustCorpAddress__detailPostTown', grantOfProbateConfig.address_town);
    // await I.fillField('#trustCorpAddress__detailCounty', grantOfProbateConfig.address_county);
    // await I.fillField('#trustCorpAddress__detailPostCode', grantOfProbateConfig.address_postcode);
    // await I.fillField('#trustCorpAddress__detailCountry', grantOfProbateConfig.address_country);

    // await I.waitForText(grantOfProbateConfig.page2_waitForAnyOtherTcPartners);
    // await I.click({css: '#anyOtherApplyingPartnersTrustCorp_Yes'});

    // await I.waitForText(grantOfProbateConfig.page2_waitForAdditionPerson, testConfig.WaitForTextTimeout);
    // await I.click('#additionalExecutorsTrustCorpList > div > button');
    // await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);
    // if (!testConfig.TestAutoDelayEnabled) {
    //   await I.wait(testConfig.ManualDelayMedium);
    // }

    // await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
    // await I.fillField('#additionalExecutorsTrustCorpList_0_additionalExecutorTrustCorpPosition', grantOfProbateConfig.page2_positionInTrustCorp);

    // await I.fillField('#probatePractitionersPositionInTrust', grantOfProbateConfig.page2_positionInTrust);
    // await I.waitForNavigationToComplete(commonConfig.continueButton, true);
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
    await expect(this.page.locator(`#titleAndClearingType-${optName}`)).toBeEnabled();
    await this.page.locator(`#titleAndClearingType-${optName}`).scrollIntoViewIfNeeded();
    await this.page.locator(`#titleAndClearingType-${optName}`).click();
    // const optLocator = {css: `#titleAndClearingType-${optName}`};
    // await I.waitForElement(optLocator);
    // await I.scrollTo(optLocator);
    // await I.click(optLocator);
    // if (!testConfig.TestAutoDelayEnabled) {
    //   await I.wait(testConfig.ManualDelayLong);
    // }
    const isNa = optName === 'TCTNoT';
    const isTrustOption = optName.startsWith('TCTTrustCorp');
    const allRenouncing = optName.endsWith('AllRenouncing');
    const isSuccessorFirm = optName === 'TCTPartSuccPowerRes' || optName === 'TCTSolePrinSucc' || optName === 'TCTPartSuccAllRenouncing' || optName === 'TCTPartSuccOthersRenouncing';

    if (!isNa && !allRenouncing && !isTrustOption && isSuccessorFirm) {
      await expect(this.firmNameWillTextLocator).toBeVisible();
      await this.firmNameInWillLocator.scrollIntoViewIfNeeded();
      // await I.waitForText('Name of firm named in will');
      // await I.scrollTo('#nameOfFirmNamedInWill');
    }
  }

  async grantOfProbatePage3() {
    await expect(this.displenseWithNoticeLeaveLocator).toBeEnabled();
    await this.runAccessibilityTest();
    await this.displenseWithNoticeLeaveLocator.click();
    await this.dispenseWithNoticeOverviewLocator.fill(grantOfProbateConfig.page3_dispenseWithNoticeOverview);
    await this.dispenseWithNoticeSupportingDocsLocator.fill(grantOfProbateConfig.page3_dispenseWithNoticeSupportingDocs);
    await this.dispenseWithNoticeOtherExecsListLocator.click();
    await this.dispenseWithNoticeExecutorNotApplyingName.fill(grantOfProbateConfig.page3_dispenseWithNoticeName);
    await this.waitForNavigationToComplete();
    // await I.waitForEnabled({css: `#dispenseWithNoticeLeaveGiven_${grantOfProbateConfig.page3_dispenseWithNoticeLeaveGiven}`});
    // await I.runAccessibilityTest();
    // await I.click(`#dispenseWithNoticeLeaveGiven_${grantOfProbateConfig.page3_dispenseWithNoticeLeaveGiven}`);

    // await I.fillField('#dispenseWithNoticeOverview', grantOfProbateConfig.page3_dispenseWithNoticeOverview);
    // await I.fillField('#dispenseWithNoticeSupportingDocs', grantOfProbateConfig.page3_dispenseWithNoticeSupportingDocs);

    // await I.click('#dispenseWithNoticeOtherExecsList > div > button');
    // await I.fillField('#dispenseWithNoticeOtherExecsList_0_notApplyingExecutorName', grantOfProbateConfig.page3_dispenseWithNoticeName);

    // await I.waitForNavigationToComplete(commonConfig.continueButton, true);
  }

  async grantOfProbatePage4(isSolicitorApplying = false) {
    // readonly postcodeLinkLocator = this.page.getByText(createCaveatConfig.UKpostcodeLink);
    // readonly solSignSot = this.page.locator(`#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`);

    await expect(this.otherExecutorExistsLocator).toBeEnabled();
    await this.runAccessibilityTest();
    // await I.waitForElement('#otherExecutorExists');
    // await I.runAccessibilityTest();

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
      await expect(this.solsAddExecutorAddressOptionLocator).toBeEnabled();
      // await I.click(`#otherExecutorExists_${grantOfProbateConfig.page4_otherExecutorExists}`);

      // await I.waitForText(grantOfProbateConfig.page2_waitForAdditionalExecutor, testConfig.WaitForTextTimeout);

      // await I.waitForText(grantOfProbateConfig.page4_previouslyIdentifiedApplyingExecutors, testConfig.WaitForTextTimeout);
      // await I.waitForText(grantOfProbateConfig.page4_previouslyIdentifiedNotApplyingExecutors, testConfig.WaitForTextTimeout);

      // await I.click('#solsAdditionalExecutorList > div > button');

      // await I.fillField('#solsAdditionalExecutorList_0_additionalExecForenames', grantOfProbateConfig.page2_executorFirstName);

      // if (!testConfig.TestAutoDelayEnabled) {
      //   // only valid for local dev where we need it to run as fast as poss to minimise
      //   // lost dev time
      //   await I.wait(testConfig.ManualDelayMedium);
      // }

      // await I.fillField('#solsAdditionalExecutorList_0_additionalExecLastname', grantOfProbateConfig.page2_executorSurname);
      // await I.click(`#solsAdditionalExecutorList_0_additionalExecNameOnWill_${grantOfProbateConfig.optionYes}`);
      // await I.waitForVisible('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', testConfig.WaitForTextTimeout);
      // await I.fillField('#solsAdditionalExecutorList_0_additionalExecAliasNameOnWill', grantOfProbateConfig.page2_executorAliasName);

      // await I.click({css: '#solsAdditionalExecutorList_0_additionalApplying_Yes'});

      // await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', testConfig.WaitForTextTimeout);
      // await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_postcodeInput', grantOfProbateConfig.page2_executorPostcode);
      // await I.click('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress > div  > div > button');

      // await I.waitForElement('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', testConfig.WaitForTextTimeout);
      // const optLocator = {css: '#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList > option:first-child'};
      // await I.waitForElement(optLocator, testConfig.WaitForTextTimeout);
      const optText = await I.grabTextFrom(optLocator);
      if (optText.indexOf(grantOfProbateConfig.noAddressFound) >= 0) {
        const addExecAddrLocator = {css: grantOfProbateConfig.page4_postcodeLink};
        await I.waitForElement(addExecAddrLocator);
        await I.waitForClickable(addExecAddrLocator);
        await I.click(addExecAddrLocator);
        await I.waitForVisible({css: '#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine1'});
        await I.fillField({css: '#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine1'}, grantOfProbateConfig.page2_executorAddress_line1);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine2', grantOfProbateConfig.page2_executorAddress_line2);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailAddressLine3', grantOfProbateConfig.page2_executorAddress_line3);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostTown', grantOfProbateConfig.page2_executorAddress_town);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailPostCode', grantOfProbateConfig.page2_executorPostcode);
        await I.fillField('#solsAdditionalExecutorList_0_additionalExecAddress__detailCountry', grantOfProbateConfig.page2_executorAddress_country);
      } else {
        await I.retry(10).selectOption('#solsAdditionalExecutorList_0_additionalExecAddress_additionalExecAddress_addressList', grantOfProbateConfig.page2_executorAddress);
      }

    } else {

      await I.click(`#otherExecutorExists_${grantOfProbateConfig.optionNo}`);

    }

    await I.waitForNavigationToComplete(commonConfig.continueButton, true);
  }
};
