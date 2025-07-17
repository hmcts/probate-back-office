import { expect, Page, TestInfo } from "@playwright/test";
import dateFns from "date-fns";
import { testConfig } from "../../Configs/config.ts";
import postPaymentReviewTabConfig from "../caseDetails/solicitorApplyProbate/postPaymentReviewTabConfig.json" with { type: "json" };
import serviceRequestTabConfig from "../caseDetails/solicitorApplyProbate/serviceRequestTabConfig.json" with { type: "json" };
import commonConfig from "../common/commonConfig.json" with { type: "json" };
import createCaveatConfig from "../createCaveat/createCaveatConfig.json" with { type: "json" };
import applicationDetailsConfig from "../solicitorApplyCaveat/applicationDetails/applicationDetails.json" with { type: "json" };
import applyCaveatConfig from "../solicitorApplyCaveat/applyCaveat/applyCaveat.json" with { type: "json" };
import completeApplicationConfig from "../solicitorApplyCaveat/completeApplication/completeApplication.json" with { type: "json" };
import applyProbateConfig from "../solicitorApplyProbate/applyProbate/applyProbateConfig.json" with { type: "json" };
import makePaymentConfig from "../solicitorApplyProbate/makePayment/makePaymentConfig.json" with { type: "json" };
import { BasePage } from "../utility/basePage.ts";

type ServiceRequestTabConfig = typeof serviceRequestTabConfig;

export class SolCreateCasePage extends BasePage {
    readonly completeApplicationSubmitButton = this.page.getByRole("button", {
      name: "Close and return to case details",
    });
    readonly serviceRequestTabLocator = this.page.getByRole("tab", {
      name: makePaymentConfig.paymentTab,
    });
    readonly reviewLinkLocator = this.page.getByText(
      makePaymentConfig.reviewLinkText
    );
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
    readonly confirmPaymentButton = this.page.getByRole("button", {
      name: "Confirm payment",
    });
    readonly serviceRequestLinkLocator = this.page.getByRole("link", {
      name: makePaymentConfig.serviceRequestLink,
      exact: true,
    });
    readonly eventHistoryTab = this.page.getByRole("tab", {
      name: makePaymentConfig.eventHistoryTab,
    });
    readonly caseProgressTabLocator = this.page.getByRole("tab", {
      name: makePaymentConfig.caseProgressTab,
      exact: true,
    });
    readonly postcodeLinkLocator = this.page.getByText(
      createCaveatConfig.UKpostcodeLink
    );
    readonly solSignSot = this.page.locator(
      `#solsSolicitorWillSignSOT_${applyProbateConfig.page2_optionNo}`
    );

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
    await expect(this.page.locator("#deceasedForenames")).toBeVisible();
    await this.runAccessibilityTest();
    await this.page
      .locator("#deceasedForenames")
      .fill(applicationDetailsConfig.page2_deceased_forename);
    await this.page
      .locator("#deceasedSurname")
      .fill(applicationDetailsConfig.page2_deceased_surname);
    await this.page
      .locator("#deceasedDateOfDeath-day")
      .fill(applicationDetailsConfig.page2_dateOfDeath_day);
    await this.page
      .locator("#deceasedDateOfDeath-month")
      .fill(applicationDetailsConfig.page2_dateOfDeath_month);
    await this.page
      .locator("#deceasedDateOfDeath-year")
      .fill(applicationDetailsConfig.page2_dateOfDeath_year);
    await this.page
      .locator(
        `#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`
      )
      .focus();
    await this.page
      .locator(
        `#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`
      )
      .click();
    await this.page
      .locator(
        `#deceasedAnyOtherNames_${applicationDetailsConfig.page2_hasAliasYes}`
      )
      .click();
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
    await this.page
      .locator("#deceasedAddress__detailAddressLine1")
      .fill(applicationDetailsConfig.address_line1);
    await this.page
      .locator("#deceasedAddress__detailAddressLine2")
      .fill(applicationDetailsConfig.address_line2);
    await this.page
      .locator("#deceasedAddress__detailAddressLine3")
      .fill(applicationDetailsConfig.address_line3);
    await this.page
      .locator("#deceasedAddress__detailPostTown")
      .fill(applicationDetailsConfig.address_town);
    await this.page
      .locator("#deceasedAddress__detailCounty")
      .fill(applicationDetailsConfig.address_county);
    await this.page
      .locator("#deceasedAddress__detailPostCode")
      .fill(applicationDetailsConfig.address_postcode);
    await this.page
      .locator("#deceasedAddress__detailCountry")
      .fill(applicationDetailsConfig.address_country);
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
    await expect(this.page.locator("#solsStartPage")).toBeVisible();
    await this.runAccessibilityTest();
    await this.waitForSubmitNavigationToComplete(commonConfig.submitButton);
    // await I.waitForElement('#solsStartPage');
    // await I.runAccessibilityTest();
    // await I.waitForNavigationToComplete(commonConfig.submitButton, true);
  }
};
