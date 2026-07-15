import { test, expect } from "../Fixtures/apiFixtures.js";
import { CaseApiService, RaiseCaveatData } from "../APIServices/caseApiService.js";

function buildRaiseCaveatData(): RaiseCaveatData {
  const timestamp = Date.now().toString();

  return {
    applicationType: "Personal",
    registryLocation: "Liverpool",
    deceasedForenames: `E2Edeceasedforenames${timestamp}`,
    deceasedSurname: `E2Edeceasedsurname${timestamp}`,
    deceasedDateOfDeath: "2017-01-01",
    deceasedAnyOtherNames: "Yes",
    deceasedFullAliasNameList: [
      {
        id: crypto.randomUUID(),
        value: {
          FullAliasName: "deceasedaliasname1",
        },
      },
      {
        id: crypto.randomUUID(),
        value: {
          FullAliasName: "deceasedaliasname2",
        },
      },
    ],
    deceasedAddress: {
      AddressLine1: "1",
      AddressLine2: "Buckingham Palace",
      AddressLine3: "The place to be",
      PostTown: "London",
      County: "London",
      PostCode: "SW1A 1AA",
      Country: "United Kingdom",
    },
    caveatorForenames: "caveatorforenames",
    caveatorSurname: "caveatorsurname",
    caveatorEmailAddress: "caveator@probate-test.com",
    solsSolicitorAppReference: "TW123-345",
    caveatorAddress: {
      AddressLine1: "1",
      AddressLine2: "Buckingham Palace",
      AddressLine3: "The place to be",
      PostTown: "London",
      County: "London",
      PostCode: "SW1A 1AA",
      Country: "United Kingdom",
    },
    languagePreferenceWelsh: "No",
    caveatRaisedEmailNotificationRequested: "Yes",
  };
}

test.describe("Case API", () => {
  test("should create a caveat case by API", async ({ page }) => {
    await page.goto("/");

    const caseApiService = new CaseApiService(page.request);

    const createdCase = await caseApiService.raiseCaveatCase(
      buildRaiseCaveatData()
    );

    const caseId =
      createdCase.id ||
      createdCase.caseId ||
      createdCase?.case_data?.id;

    expect(createdCase).toBeTruthy();
    expect(caseId, "Case ID should exist after case creation").toBeTruthy();
  });

  test("should raise a caveat case and get case data by API", async ({ page }) => {
    await page.goto("/");

    const caseApiService = new CaseApiService(page.request);

    const createResponse = await caseApiService.raiseCaveatCase(
      buildRaiseCaveatData()
    );

    const caseId =
      createResponse.id ||
      createResponse.caseId ||
      createResponse?.case_data?.id;

    expect(caseId, "Case ID should exist after case creation").toBeTruthy();

    const caseData = await caseApiService.getCaseData(String(caseId));

    expect(caseData).toBeTruthy();
    expect(
      caseData.id || caseData.case_id || caseData.caseId,
      "Fetched case should contain the same case id"
    ).toBeTruthy();
  });
});
