import { expect } from "@playwright/test";
import { BasePage } from "../utility/basePage.ts";

export class TasksPage extends BasePage {
  readonly priorityLocator = this.page.locator(
    "//span[text()='Priority']/../..//strong",
  );
  readonly dueDateLocator = this.page.locator(
    "//span[text()='Due date']/../..//dd/span",
  );
  readonly assignedToLocator = this.page.locator(
    "//span[text()='Assigned to']/../..//dd",
  );
  readonly assignTaskLinkLocator = this.page.locator(
    "//a[@id='action_assign']",
  );
  readonly assignToMeLinkLocator = this.page.locator("//a[@id='action_claim']");
  readonly reassignTaskLinkLocator = this.page.locator(
    "//a[@id='action_reassign']",
  );
  readonly unassignTaskLinkLocator = this.page.locator(
    "//a[@id='action_unclaim']",
  );
  readonly cancelTaskLinkLocator = this.page.locator(
    "//a[@id='action_cancel']",
  );
  readonly markAsDoneLinkLocator = this.page.locator(
    "//a[@id='action_complete']",
  );
  readonly nextStepsLocator = this.page.locator(
    "//span[text()='Next steps']/../..//a",
  );

  //flicks between tabs to check if the task is displayed in the tasks tab on the case
  async verifyTaskIsDisplayed(taskName: string) {
    let taskVisible = false;
    const taskLocator = `//exui-case-task[./*[normalize-space()='${taskName}']][1]`;
    const startTime = Date.now();
    const timeout = 200000;

    while (Date.now() - startTime < timeout) {
      taskVisible = await this.page.isVisible(taskLocator);
      if (taskVisible) {
        break;
      }
      await this.page.getByRole("tab", { name: "Event History" }).click();
      await this.page.waitForTimeout(30000);
      await this.page.getByRole("tab", { name: "Tasks" }).click();
      await this.page.waitForTimeout(2000);
    }

    if (!taskVisible) {
      throw new Error(
        `Task "${taskName}" is not displayed within ${timeout / 1000} seconds.`,
      );
    }
  }

  async verifyTaskIsHidden(taskName: string) {
    let taskVisible = true;
    await this.page.getByRole("tab", { name: "Tasks" }).click();

    const taskLocator = `//exui-case-task[./*[normalize-space()='${taskName}']][1]`;
    const startTime = Date.now();
    const timeout = 200000;

    while (Date.now() - startTime < timeout) {
      taskVisible = await this.page.isVisible(taskLocator);
      if (!taskVisible) {
        break;
      }
      await this.page.getByRole("tab", { name: "Event History" }).click();
      await this.page.waitForTimeout(30000);
      await this.page.getByRole("tab", { name: "Tasks" }).click();
      await this.page.waitForTimeout(2000);
    }

    if (taskVisible) {
      throw new Error(
        `Task "${taskName}" is still displayed after waiting for ${timeout / 1000} seconds.`,
      );
    }
  }

  async assignTaskToSelf(user: string) {
    await this.assignToMeLinkLocator.click();
    await expect(this.assignedToLocator).toHaveText(user);
  }

  async verifyUnassignedTaskData(workingDays: number, priority: string) {
    let dueDate = this.calculateDueDate(workingDays);
    await expect(this.priorityLocator).toHaveText(priority);
    await expect(this.dueDateLocator).toHaveText(dueDate);
    await expect(this.assignedToLocator).toHaveText("Unassigned");
    await expect(this.assignTaskLinkLocator).toBeVisible();
    await expect(this.assignToMeLinkLocator).toBeVisible();
  }

  async verifyAssignedTaskData(isTeamLeader: boolean) {
    await expect(this.unassignTaskLinkLocator).toBeVisible();
    await expect(this.reassignTaskLinkLocator).toBeVisible();

    if (isTeamLeader) {
      await expect(this.cancelTaskLinkLocator).toBeVisible();
      await expect(this.markAsDoneLinkLocator).toBeVisible();
    }
  }

  //gets all next steps and compares them to the expected options passed in as an array of strings
  async verifyNextStepsOptions(options: string[]) {
    const availableOptions = await this.nextStepsLocator.allTextContents();
    const trimmedOptions = availableOptions.map((text) => text.trim());
    expect(trimmedOptions).toMatchObject(options);
  }

  async triggerNextStepEvent(nextStep: string) {
    const nextStepLocator = this.page.locator(`//a[text()='${nextStep}']`);

    await expect(nextStepLocator).toBeVisible();
    await expect(nextStepLocator).toBeEnabled();

    let attempts = 0;
    const maxAttempts = 3;
    //used a while loop here as sometimes the link doesn't trigger event and page just refreshes.
    while (this.page.url().includes("Tasks") && attempts < maxAttempts) {
      await nextStepLocator.click();
      await this.page.waitForTimeout(500);

      attempts++;
    }
  }
}
