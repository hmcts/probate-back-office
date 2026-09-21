import { test } from "../../../Fixtures/fixtures.ts";
import { createSolicitorCase } from "./solicitorCaseCreationHelper.ts";

test.describe("Solicitor - Grant of Representation Case Creation", () => {
  test("Solicitor - Grant of Representation Case Creation @webkit", async ({
    basePage,
    signInPage,
    createCasePage,
    solCreateCasePage,
    cwEventActionsPage
  }) => {
    test.setTimeout(300000);
    const caseTypeKey = (process.env.CASE_TYPE ?? 'probate').toLowerCase();

    const caseRef = await createSolicitorCase(
      { basePage, signInPage, createCasePage, solCreateCasePage, cwEventActionsPage },
      caseTypeKey
    );

    console.log(`Case ${caseRef} created successfully and reached Awaiting Documentation`);
  });
});
