import { APIRequestContext, expect } from "@playwright/test";
import { BasePage } from "../utility/basePage.ts";

//the logic used here to verify the task is displayed correctly currently just checks the top row of the table. 
//this is fine for now as only one task is expected to display. However in the future if multiple tasks are expected to display then the logic will need revisiting. 
export class MyWorkPage extends BasePage {
  readonly goToTaskLinkLocator = this.page.locator("//a[@id='action_go']");
  readonly reassignTaskLinkLocator = this.page.locator(
    "//a[@id='action_reassign']",
  );
  readonly unAssignTaskLinkLocator = this.page.locator(
    "//a[@id='action_unclaim']",
  );
  readonly topRowTaskLocator = this.page.locator("xpath=//tbody/tr").first();
  readonly activeTasksHeadingLocator = this.page.locator("//h2[text()='Active tasks']");

  async verifyTaskIsDisplayedOnMyWorkPage(
    expectedTaskName: string,
    workingDaysToComplete: number,
    expectedPriority: string,
    request: APIRequestContext
  ) {
    const row = this.topRowTaskLocator;
    const taskNameValue = row.locator("xpath=./td[5]");
    const dueDateValue = row.locator("xpath=./td[6]");
    const priorityValue = (
      await row.locator("xpath=./td[8]").innerText()
    ).toLocaleLowerCase();
    const manageButton = row.locator("xpath=./td[9]//button");
    const expectedDueDate = await this.calculateDueDate(workingDaysToComplete, request);

    await expect(taskNameValue).toHaveText(expectedTaskName);
    await expect(dueDateValue).toHaveText(expectedDueDate);
    expect(priorityValue).toEqual(expectedPriority);
    await expect(manageButton).toBeVisible();

    await manageButton.click();
    await expect(this.goToTaskLinkLocator).toBeVisible();
    await expect(this.reassignTaskLinkLocator).toBeVisible();
    await expect(this.unAssignTaskLinkLocator).toBeVisible();
  }

  async goToTopRowTask() {
    const row = this.topRowTaskLocator;
    const manageButton = row.locator("xpath=./td[9]//button");
    if (await this.goToTaskLinkLocator.isHidden()) await manageButton.click();
    await expect(this.goToTaskLinkLocator).toBeVisible();
    await this.goToTaskLinkLocator.click();
    await expect(this.activeTasksHeadingLocator).toBeVisible();
  }
}
