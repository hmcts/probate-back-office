import { AxeUtils } from "@hmcts/playwright-common";
import { expect, Page, TestInfo } from "@playwright/test";
import { testConfig } from "../../Configs/config.ts";
import commonConfig from "../common/commonConfig.json" with { type: "json" };

export class BasePage {
  readonly rejectLocator = this.page.getByRole("button", {
    name: "Reject analytics cookies",
  });
  readonly submitButtonLocator = this.page.getByRole("button", {
    name: "Submit",
  });
  readonly goButtonLocator = this.page.getByRole("button", { name: "Go" });

  constructor(public readonly page: Page) {}

  async logInfo(scenarioName: string, log: string, caseRef: string) {
    let ret = scenarioName;
    await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
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
          await this.page.waitForTimeout(testConfig.RejectCookieDelay);
        }
      } catch (e) {
        console.error(`error trying to close cookie banner: ${e.message}`);
      }
    }
  }

  async getCaseRefFromUrl() {
    await this.page.waitForTimeout(testConfig.GetCaseRefFromUrlDelay);
    const url = this.page.url();
    return url
      .replace("#Event%20History", "")
      .replace("#Case%20Progress", "")
      .split("/")
      .pop()
      .match(/.{4}/g)
      .join("-");
  }

  async getSacCaseRef() {
    await this.page.locator('//div[@class="column-one-half"]//ccd-case-header').textContent();
  }

  async waitForNavigationToComplete(buttonLocator) {
    // const navigationPromise = this.page.waitForNavigation();
    await expect(this.page.locator(buttonLocator)).toBeVisible();
    await expect(this.page.locator(buttonLocator)).toBeEnabled();
    await this.page.locator(buttonLocator).click();
    // await this.page.waitForTimeout(1000);
    await this.page.waitForLoadState('domcontentloaded');
    // await this.page.waitForTimeout(1000);
    // await navigationPromise;
  }

  async waitForStopNavigationToComplete(buttonLocator) {
    await expect(this.page.locator(buttonLocator)).toBeVisible();
    await expect(this.page.locator(buttonLocator)).toBeEnabled();
    await this.page.locator(buttonLocator).click({ noWaitAfter: true });
    await this.page.waitForTimeout(1000);
  }

  async waitForGoNavigationToComplete() {
    await expect(this.goButtonLocator).toBeVisible();
    await expect(this.goButtonLocator).toBeEnabled();
    await Promise.all([
      this.goButtonLocator.waitFor({ state: "visible" }),
      this.goButtonLocator.click(),
      this.goButtonLocator.waitFor({ state: "detached", timeout: 10000 }),
    ]);
  }

  async waitForSignOutNavigationToComplete(signOutLocator: string) {
    // const navigationPromise = this.page.waitForNavigation();
    await expect(this.page.locator(`${signOutLocator}`)).toBeVisible();
    await expect(this.page.locator(`${signOutLocator}`)).toBeEnabled();
    await this.page.locator(`${signOutLocator}`).click();
    await this.page.waitForLoadState("domcontentloaded")
    // await navigationPromise;
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
    await this.page.waitForTimeout(delay);

    // *****Need to comment this until accessibility script is completed*****/
    await this.runAccessibilityTest();

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
    await this.runAccessibilityTest();

    if (tabUpdates) {
      const updatedConfig = tabConfigFile[tabUpdates];
      let fields = updatedConfig.fields;
      let keys = updatedConfig.dataKeys;
      if (forUpdateApplication) {
        fields = fields.concat(updatedConfig.updateAppFields);
        keys = keys.concat(updatedConfig.updateAppDataKeys);
      }

      for (let i = 0; i < fields.length; i++) {
        await expect(this.page.getByText(fields[i]).first()).toBeVisible();
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
    const delay = testConfig.CaseDetailsDelayDefault;

    if (tabConfigFile.tabName) {
      await expect(this.page.locator(`//div[contains(text(),"${tabConfigFile.tabName}")]`)).toBeEnabled();
      // const tabXPath = `//div[contains(text(),"${tabConfigFile.tabName}")]`;
      // Tabs are hidden when there are more tabs
      // await I.waitForElement(tabXPath, tabConfigFile.testTimeToWaitForTab || 60);
    }
    await expect(this.page.getByRole("heading", { name: caseRef })).toBeVisible();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).focus();
    await this.page.getByRole("tab", { name: tabConfigFile.tabName }).click();
    await this.page.waitForTimeout(delay);
    await this.runAccessibilityTest();
    // await I.waitForText(caseRef, testConfig.WaitForTextTimeout || 60);
    // await I.clickTab(tabConfigFile.tabName);
    // await I.wait(delay);
    // await I.runAccessibilityTest();

    if (tabConfigFile.waitForText) {
      await expect(this.page.getByLabel(tabConfigFile.waitForText)).toBeVisible();
      // await I.waitForText(tabConfigFile.waitForText, testConfig.WaitForTextTimeout || 60);
    }

    /* eslint-disable no-await-in-loop */
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
    const locs = await this.page.getByText("Cancel upload").all();
    for (let i = 0; i < locs.length; i++) {
      await expect(locs[i]).toBeDisabled();
    }
  }

  async caseProgressContinueWithoutChangingAnything(numTimes = 1) {
    for (let i=0; i < numTimes; i++) {
      await expect(this.page.locator(commonConfig.continueButton)).toBeEnabled()
      await this.waitForNavigationToComplete(commonConfig.continueButton);
      // await I.waitForElement({css: commonConfig.continueButton});
      // await I.waitForNavigationToComplete(commonConfig.continueButton,testConfig.CaseProgressContinueWithoutChangingDelay);
    }
  }
}
