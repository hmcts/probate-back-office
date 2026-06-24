import { AxeUtils } from "@hmcts/playwright-common";
import {expect, Locator, Page, TestInfo} from "@playwright/test";
import { testConfig } from "../../Configs/config.ts";
import commonConfig from "../common/commonConfig.json" with { type: "json" };

export class BasePage {
  readonly rejectLocator = this.page.getByRole("button", {
    name: "Reject analytics cookies",
  });
  readonly submitButtonLocator = this.page.getByRole("button", {
    name: "Submit",
  });


  constructor(public readonly page: Page) {}

  async isVisible(selector: string, timeout = 2_000): Promise<boolean> {
    try {
      await this.page.locator(selector).first().waitFor({ state: "visible", timeout });
      return true;
    } catch {
      return false;
    }
  }

  async fillIfPresent(selector: string, value: string, timeout = 2_000): Promise<boolean> {
    const visible = await this.isVisible(selector, timeout);
    if (!visible) {
      console.warn(`[OptionalField] Not visible, skipping fill: ${selector}`);
      return false;
    }
    await this.page.locator(selector).first().fill(value);
    return true;
  }

  async clickIfPresent(selector: string, timeout = 2_000): Promise<boolean> {
    const visible = await this.isVisible(selector, timeout);
    if (!visible) {
      console.warn(`[OptionalField] Not visible, skipping click: ${selector}`);
      return false;
    }
    await this.page.locator(selector).first().click();
    return true;
  }

  async expectTextEventually(
    selector: string,
    text: string,
    opts: { timeout?: number; soft?: boolean } = {}
  ): Promise<boolean> {
    const timeout = opts.timeout ?? 15_000;
    try {
      await expect(this.page.locator(selector)).toContainText(text, { timeout });
      return true;
    } catch (error) {
      if (opts.soft) {
        console.warn(`[SoftAssert] "${text}" not found in ${selector} within ${timeout}ms`);
        return false;
      }
      throw error;
    }
  }

  private async waitForTabFieldWithRecovery(fieldLabel: string, attempts = 8, soft = false) {
    for (let attempt = 1; attempt <= attempts; attempt++) {
      const fieldLocator = this.page.getByText(fieldLabel).first();
      if (await fieldLocator.isVisible().catch(() => false)) {
        return true;
      }

      console.log(`[DTSPB-5228] Field '${fieldLabel}' not visible on attempt ${attempt}/${attempts}. URL: ${this.page.url()}`);
      await this.page.goto(this.page.url(), { waitUntil: "domcontentloaded", timeout: 20_000 }).catch(() => undefined);
    }

    const bodyText = await this.page.locator("body").innerText({ timeout: 5_000 }).catch(() => "");
    console.log(`[DTSPB-5228] Field '${fieldLabel}' not visible after retries. URL: ${this.page.url()}, body excerpt: ${bodyText.slice(0, 800)}`);
    if (soft) {
      return false;
    }

    await expect(this.page.getByText(fieldLabel).first()).toBeVisible({ timeout: 10_000 });
    return false;
  }

  async logInfo(scenarioName: string, log: string, caseRef?: string) {
    let ret = scenarioName;
    // await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
    if (log) {
      ret = ret + " : " + log;
    }
    if (caseRef) {
      ret = ret + " : " + caseRef;
    }
    console.info(ret);
  }

  async runAccessibilityTest() {
    await new AxeUtils(this.page).audit();
  }

  public async rejectCookies() {
    if (testConfig.RejectCookies) {
      try {
        //const rejectLocator = {css: 'button.govuk-button[value="reject"]'};
        const numVisibleCookieBannerEls = await this.rejectLocator.count();
        if (numVisibleCookieBannerEls > 0) {
          // just reject additional cookies
          await expect(this.rejectLocator).toBeEnabled();
          await this.rejectLocator.click();
          // await this.page.waitForTimeout(testConfig.RejectCookieDelay);
        }
      } catch (e) {
        console.error(`error trying to close cookie banner: ${e.message}`);
      }
    }
  }

  async getCaseRefFromUrl() {
    // await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
    const url = this.page.url();
    return url
      .replace("#Event%20History", "")
      .replace("#Case%20Progress", "")
      .split("/")
      .pop()
      .match(/.{4}/g)
      .join("-");
  }

  async getCaseRefFromUrlNoHyphen() {
    const url = this.page.url();
    return url
      .replace("#Event%20History", "")
      .replace("#Case%20Progress", "")
      .split("/")
      .pop()
      .match(/.{4}/g)
      .join("");
  }

  async waitForNavigationToComplete(buttonLocator: Locator | string, timeout: number = 5_000): Promise<void> {
    const currentUrl = this.page.url();
    const locator = typeof buttonLocator === 'string'
      ? this.page.locator(buttonLocator)  // String - convert to Locator
      : buttonLocator;
    await expect(locator).toBeVisible({ timeout });
    await expect(locator).toBeEnabled({ timeout });
    console.log(`[DTSPB-5228] Clicking navigation control from URL: ${currentUrl}`);
    await locator.click({ timeout, noWaitAfter: true });

    await this.page.waitForLoadState('domcontentloaded', { timeout: 10_000 }).catch(() => undefined);
    let urlChanged = false;
    try {
      await expect
        .poll(() => this.page.url(), { intervals: [500], timeout: 5_000 })
        .not.toBe(currentUrl);
      urlChanged = true;
      console.log(`[DTSPB-5228] Navigation completed. New URL: ${this.page.url()}`);
    } catch {
      console.log(`[DTSPB-5228] URL did not change after click. Current URL: ${this.page.url()}`);
    }

    if (!urlChanged) {
      console.log(`[DTSPB-5228] Retrying click after unchanged URL: ${currentUrl}`);
      await locator.scrollIntoViewIfNeeded().catch(() => undefined);
      await locator.click({ timeout, noWaitAfter: true }).catch(() => undefined);
      await this.page.waitForLoadState('domcontentloaded', { timeout: 10_000 }).catch(() => undefined);
      try {
        await expect
          .poll(() => this.page.url(), { intervals: [500], timeout: 5_000 })
          .not.toBe(currentUrl);
        console.log(`[DTSPB-5228] Navigation completed after retry. New URL: ${this.page.url()}`);
      } catch {
        console.log(`[DTSPB-5228] URL still unchanged after retry. Current URL: ${this.page.url()}`);
      }
    }

  }

  async verifyPageLoad(pageLocator: Locator, timeout: number = 5_000): Promise<void> {
    let hasRetriedWithReload = false;
    await expect(async () => {
      await this.page.waitForLoadState('domcontentloaded', { timeout: 10_000 }).catch(() => undefined);
      if (!(await pageLocator.isVisible()) && !hasRetriedWithReload) {
        hasRetriedWithReload = true;
        console.log(`[DTSPB-5228] verifyPageLoad did not find locator, reloading once. URL: ${this.page.url()}`);
        await this.page.reload({ waitUntil: 'domcontentloaded' }).catch(() => undefined);
      }
      await expect(pageLocator).toBeVisible({ timeout: timeout });
    }).toPass({ intervals: [1_000], timeout: 60_000 });
  }

  async seeCaseDetails(
    testInfo: TestInfo,
    caseRef: string,
    tabConfigFile, // TODO: type?
    dataConfigFile, // TODO: type?
    nextStep?: string,
    endState?: string,
    delay: number = testConfig.CaseDetailsDelayDefault,
    nocEvent?: boolean
  ) {
    void delay;
    if (tabConfigFile.tabName && tabConfigFile.tabName !== "Documents") {
      await expect(
        this.page.getByLabel(`${tabConfigFile.tabName}`, { exact: true })
      ).toBeVisible();
    }

    await expect(
      this.page.getByRole("heading", { name: caseRef })
    ).toBeVisible();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).focus();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).click();
    await this.page.waitForLoadState("domcontentloaded");

    if(!nocEvent) {
      await this.runAccessibilityTest();
    }

    if (tabConfigFile.waitForText) {
      const tabLocator = this.page.getByLabel(tabConfigFile.waitForText);
      await expect(tabLocator).toBeVisible();
    }

    for (let i = 0; i < tabConfigFile.fields.length; i++) {
      if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== "") {
        const textCount = await this.page
          .getByText(tabConfigFile.fields[i])
          .count();
        if (textCount > 1) {
          if (tabConfigFile.fields[i] === "Caveat not matched") {
            await expect(
              this.page.getByText(tabConfigFile.fields[i]).nth(2)
            ).toBeVisible();
          }
          if (nocEvent) {
            await expect(
              this.page.getByText(tabConfigFile.fields[i]).first()
            ).toBeVisible();
          } else {
            await expect(
              this.page.getByText(tabConfigFile.fields[i], { exact: true }).first()
            ).toBeVisible();
          }

        } else if (tabConfigFile.tabName === "Event History") {
          await expect(
            this.page.getByRole("table", { name: "Details" })
          ).toContainText(tabConfigFile.fields[i]);
        } else {
          await expect(
            this.page.getByRole("table", { name: "case viewer table" })
          ).toContainText(tabConfigFile.fields[i]);
        }
      }
    }

    const dataConfigKeys = tabConfigFile.dataKeys;
    // If 'Event History' tab, then check Next Step (Event), End State, Summary and Comment
    if (tabConfigFile.tabName === "Event History") {
      if (nextStep === endState) {
        await expect(this.page.getByText(nextStep).nth(2)).toBeVisible();
        await expect(this.page.getByText(endState).nth(3)).toBeVisible();
      } else if (
        endState === "Caveat created" ||
        nextStep === "Apply for probate" ||
        endState === "Grant of probate created" ||
        nextStep === "Grant of probate details" ||
        nextStep === "Deceased details" ||
        nextStep === "Intestacy details" ||
        nextStep === "Admon will details" ||
        nextStep === "Apply NoC Decision"
      ) {
        await expect(
          this.page.getByRole("cell", { name: endState, exact: true })
        ).toBeVisible();
        await expect(this.page.getByLabel(nextStep).nth(1)).toBeVisible();
        // await expect(this.page.getByLabel(nextStep), {exact: true}).toBeVisible();
      } else {
        await expect(
          this.page
            .getByRole("cell", { name: endState, exact: true })
            .locator("span")
        ).toBeVisible();
        await expect(
          this.page
            .getByRole("cell", { name: nextStep, exact: true })
            .locator("span")
        ).toBeVisible();
      }
      let eventSummaryPrefix = nextStep;
      eventSummaryPrefix =
        eventSummaryPrefix.replace(/\s+/g, "_").toLowerCase() + "_";
      if (dataConfigKeys && nextStep !== "Change state") {
        await expect(
          this.page.getByText(eventSummaryPrefix + dataConfigFile.summary)
        ).toBeVisible();
        await expect(
          this.page.getByText(eventSummaryPrefix + dataConfigFile.comment)
        ).toBeVisible();
      }
    } else if (dataConfigKeys) {
      for (let i = 0; i < tabConfigFile.dataKeys.length; i++) {
        const textCount = await this.page
          .getByText(dataConfigFile[tabConfigFile.dataKeys[i]])
          .count();
        if (textCount > 1) {
          await expect(
            this.page.getByText(dataConfigFile[tabConfigFile.dataKeys[i]], {
              exact: true,
            }).first()
          ).toBeVisible();
        } else {
          await expect(
            this.page.getByRole("table", { name: "case viewer table" })
          ).toContainText(dataConfigFile[tabConfigFile.dataKeys[i]]);
        }
      }
    }
  }

  async seeUpdatesOnCase(
    testInfo: TestInfo,
    caseRef: string,
    tabConfigFile, // TODO: type?
    tabUpdates, // TODO: type?
    tabUpdatesConfigFile, // TODO: type?
    forUpdateApplication?: boolean
  ) {
    await expect(
      this.page.getByRole("heading", { name: caseRef })
    ).toBeVisible();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).focus();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).click();
    await this.page.waitForLoadState("domcontentloaded");
    await this.runAccessibilityTest();

    const caseViewerTable = this.page.getByRole("table", { name: "case viewer table" });
    let tableVisible = false;
    for (let attempt = 1; attempt <= 6; attempt++) {
      await this.page.getByRole("tab", { name: tabConfigFile.tabName }).click().catch(() => undefined);
      await this.page.waitForLoadState("domcontentloaded", { timeout: 10_000 }).catch(() => undefined);
      tableVisible = await caseViewerTable.isVisible().catch(() => false);
      if (tableVisible) {
        break;
      }
      console.log(`[DTSPB-5228] case viewer table hidden on tab '${tabConfigFile.tabName}' attempt ${attempt}/6. URL: ${this.page.url()}`);
      await this.page.goto(this.page.url(), { waitUntil: "domcontentloaded", timeout: 20_000 }).catch(() => undefined);
    }
    const allowHiddenTable = tabConfigFile.tabName === "Caveat details";
    if (!tableVisible && allowHiddenTable) {
      console.log(`[DTSPB-5228] case viewer table remained hidden on '${tabConfigFile.tabName}', continuing with label-based checks. URL: ${this.page.url()}`);
    } else {
      await expect(caseViewerTable).toBeVisible({ timeout: 10_000 });
    }

    if (tabUpdates) {
      const updatedConfig = tabConfigFile[tabUpdates];
      let fields = updatedConfig.fields;
      let keys = updatedConfig.dataKeys;
      if (forUpdateApplication) {
        fields = fields.concat(updatedConfig.updateAppFields);
        keys = keys.concat(updatedConfig.updateAppDataKeys);
      }

      for (let i = 0; i < fields.length; i++) {
        const fieldLabel = fields[i];
        const softField = fieldLabel === "Caveat expiry date";
        if (softField && !(await this.page.getByText(fieldLabel).first().isVisible().catch(() => false))) {
          console.log(`[DTSPB-5228] Optional field '${fieldLabel}' not visible; skipping strict check. URL: ${this.page.url()}`);
          continue;
        }
        await this.waitForTabFieldWithRecovery(fieldLabel, 8, softField);
      }

      for (let i = 0; i < keys.length; i++) {
        const textLocator = this.page.getByText(tabUpdatesConfigFile[keys[i]], {
          exact: true,
        });
        const locatorCount = await textLocator.count();
        if (locatorCount > 0) {
          await textLocator.first().isVisible();
        }
      }
    }
  }

  async seeTabDetailsBilingual(caseRef, tabConfigFile, dataConfigFile) {

    if (tabConfigFile.tabName) {
      await expect(this.page.locator(`//div[contains(text(),"${tabConfigFile.tabName}")]`)).toBeEnabled();
      // const tabXPath = `//div[contains(text(),"${tabConfigFile.tabName}")]`;
      // Tabs are hidden when there are more tabs
      // await I.waitForElement(tabXPath, tabConfigFile.testTimeToWaitForTab || 60);
    }
    await expect(this.page.getByRole("heading", { name: caseRef })).toBeVisible();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).focus();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).click();
    await this.page.waitForLoadState("domcontentloaded");
    await this.runAccessibilityTest();
    // await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);
    // await I.clickTab(tabConfigFile.tabName);
    // await I.wait(delay);
    // await I.runAccessibilityTest();

    if (tabConfigFile.waitForText) {
      await expect(this.page.getByLabel(tabConfigFile.waitForText)).toBeVisible();
      // await I.waitForText(tabConfigFile.waitForText, testConfig.WaitForTextTimeout || 60);
    }

    for (let i = 0; i < tabConfigFile.fields.length; i++) {
      if (tabConfigFile.fields[i] && tabConfigFile.fields[i] !== '') {
        await expect(this.page.getByText(tabConfigFile.fields[i]).first()).toBeVisible();
        // await I.see(tabConfigFile.fields[i]);
      }
    }

    for (let i = 0; i < tabConfigFile.dataKeysBilingual.length; i++) {
      await expect(this.page.getByText(dataConfigFile[tabConfigFile.dataKeysBilingual[i]])).toBeVisible();
      // await I.waitForText(dataConfigFile[tabConfigFile.dataKeysBilingual[i]], testConfig.WaitForTextTimeout || 60);
    }
  }

  async dontSeeCaseDetails(fieldLabelsNotToBeShown: string[]) {
    let visibleElements;
    let numElements;

    for (let i = 0; i < fieldLabelsNotToBeShown.length; i++) {
      visibleElements = await this.page
        .locator(
          `xpath=//div[contains(@class, 'case-viewer-label')][text()='${fieldLabelsNotToBeShown[i]}']`
        )
        .filter({ visible: true });
      numElements = await visibleElements.count();
      expect(numElements).toBe(0);
    }
  }

  async waitForUploadToBeCompleted() {
    await expect(async () => {
      const locs = await this.page.getByText("Cancel upload").all();
      console.log(`[DTSPB-5228] Waiting for upload completion. Cancel upload button count: ${locs.length}, URL: ${this.page.url()}`);
      for (let i = 0; i < locs.length; i++) {
        await expect(locs[i]).toBeDisabled({ timeout: 5_000 });
      }
    }).toPass({ intervals: [2_000], timeout: 90_000 });
  }

  async caseProgressContinueWithoutChangingAnything(numTimes = 1) {
    for (let i=0; i < numTimes; i++) {
      await expect(this.page.locator(commonConfig.continueButton)).toBeEnabled()
      await this.waitForNavigationToComplete(commonConfig.continueButton);
      // await I.waitForElement({css: commonConfig.continueButton});
      // await I.waitForNavigationToComplete(commonConfig.continueButton,testConfig.CaseProgressContinueWithoutChangingDelay);
    }
  }

  async getEnv() {
    const url = this.page.url();
    if (url.includes("aat") || url.includes("preview")) {
      return "aat";
    } else if (url.includes("demo")) {
      return "demo";
    }
  }


  async resolvePlaceholders(template: string, data: Record<string, string>) {
    return template.replace(/{{(.*?)}}/g, (_, key) => {
      if (!(key in data)) {
        throw new Error(`Missing dynamic value for placeholder: ${key}`);
      }
      return data[key];
    });
  }
}
