import dateFns from "date-fns";
import { test, expect } from "../../../Fixtures/apiFixtures.ts";

import createCaveatConfig from "../../../Pages/createCaveat/createCaveatConfig.json" with { type: "json" };
import emailCaveatorConfig from "../../../Pages/emailNotifications/caveat/emailCaveatorConfig.json" with { type: "json" };
import documentUploadConfig from "../../../Pages/documentUpload/caveat/documentUploadConfig.json" with { type: "json" };
import caseMatchesConfig from "../../../Pages/caseMatches/caveat/caseMatchesConfig.json" with { type: "json" };

type CaseData = Record<string, unknown>;

test.describe("Caseworker Caveat1 - Order summons", () => {
  test.only(
    "Caseworker Caveat1 - Order summons @api @galaxys4 @ipadpro11",
    async ({ caseApiService }) => {
      test.setTimeout(300000);

      const scenarioName = "Caseworker Caveat1 - Order summons";
      const userId = process.env.CW_USER_EMAIL!;
      const jurisdictionId = process.env.CCD_JURISDICTION ?? "PROBATE";
      const uniqueDeceasedUser = Date.now().toString();

      console.log(`Starting scenario: ${scenarioName}`);

      const createPayload: CaseData = {
        ...createCaveatConfig,
        deceasedSurname: `AutoSurname${uniqueDeceasedUser}`,
        deceasedForenames: `AutoForename${uniqueDeceasedUser}`,
      };

      const createdCase = await caseApiService.createCaveatCase(
        userId,
        jurisdictionId,
        "createCaveat",
        createPayload,
        "Raise a caveat",
        "Raise a caveat"
      );

      console.log(
        "SPIKE - createCaveatCase response:",
        JSON.stringify(createdCase, null, 2)
      );

      const caseId =
        (createdCase.id as string | undefined) ??
        (createdCase.caseId as string | undefined) ??
        ((createdCase.case_data as Record<string, unknown> | undefined)?.id as
          | string
          | undefined);

      expect(caseId).toBeTruthy();

      const getLatestCaseData = async (): Promise<CaseData> => {
        const response = await caseApiService.getCaveatCase(
          userId,
          jurisdictionId,
          String(caseId)
        );

        console.log(
          "SPIKE - getCaveatCase response:",
          JSON.stringify(response, null, 2)
        );

        return (
          (response.data as CaseData | undefined) ??
          (response.case_data as CaseData | undefined) ??
          response
        );
      };

      const submitCaveatEvent = async (
        eventId: string,
        summary: string,
        description: string,
        overrides: CaseData = {}
      ) => {
        const currentCaseData = await getLatestCaseData();

        return caseApiService.submitEvent(
          userId,
          jurisdictionId,
          "Caveat",
          String(caseId),
          eventId,
          {
            ...currentCaseData,
            ...overrides,
          },
          {
            summary,
            description,
          }
        );
      };

      await test.step("Registrar's decision", async () => {
        const response = await submitCaveatEvent(
          "registrarsDecision",
          "Registrar's decision",
          "Registrar's decision"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Email caveator", async () => {
        emailCaveatorConfig.dateAdded = dateFns.format(new Date(), "dd MMM yyyy");

        const response = await submitCaveatEvent(
          "emailCaveator",
          "Email caveator",
          "Email caveator",
          emailCaveatorConfig as CaseData
        );

        expect(response).toBeTruthy();
      });

      await test.step("Caveat match", async () => {
        const response = await submitCaveatEvent(
          "caveatMatch",
          "Caveat match",
          "Caveat match",
          caseMatchesConfig as CaseData
        );

        expect(response).toBeTruthy();
      });

      await test.step("Email caveator again", async () => {
        emailCaveatorConfig.dateAdded = dateFns.format(new Date(), "dd MMM yyyy");

        const response = await submitCaveatEvent(
          "emailCaveator",
          "Email caveator",
          "Email caveator",
          emailCaveatorConfig as CaseData
        );

        expect(response).toBeTruthy();
      });

      await test.step("Upload document", async () => {
        const response = await submitCaveatEvent(
          "uploadDocument",
          "Upload document",
          "Upload document",
          documentUploadConfig as CaseData
        );

        expect(response).toBeTruthy();
      });

      await test.step("Add comment", async () => {
        const response = await submitCaveatEvent(
          "addComment",
          "Add comment",
          "Add comment"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Await caveat resolution", async () => {
        const response = await submitCaveatEvent(
          "awaitCaveatResolution",
          "Await caveat resolution",
          "Await caveat resolution"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Warning requested", async () => {
        const response = await submitCaveatEvent(
          "warningRequested",
          "Warning requested",
          "Warning requested"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Issue caveat warning", async () => {
        const response = await submitCaveatEvent(
          "issueCaveatWarning",
          "Issue caveat warning",
          "Issue caveat warning"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Order summons", async () => {
        const response = await submitCaveatEvent(
          "orderSummons",
          "Order summons",
          "Order summons"
        );

        expect(response).toBeTruthy();
      });

      await test.step("Amend caveat details", async () => {
        createCaveatConfig.caveat_expiry_date = dateFns.format(
          dateFns.addMonths(new Date(), 6),
          "dd MMM yyyy"
        );

        const amendedData: CaseData = {
          ...createCaveatConfig,
          deceasedSurname: `UpdatedSurname${uniqueDeceasedUser}`,
          deceasedForenames: `UpdatedForename${uniqueDeceasedUser}`,
        };

        const response = await submitCaveatEvent(
          "amendCaveatDetails",
          "Amend caveat details",
          "Amend caveat details",
          amendedData
        );

        expect(response).toBeTruthy();
      });

      await test.step("Withdraw caveat", async () => {
        const response = await submitCaveatEvent(
          "withdrawCaveat",
          "Withdraw caveat",
          "Withdraw caveat"
        );

        expect(response).toBeTruthy();
      });

      const finalCase = await caseApiService.getCaveatCase(
        userId,
        jurisdictionId,
        String(caseId)
      );

      console.log(
        "SPIKE - final caveat case response:",
        JSON.stringify(finalCase, null, 2)
      );

      expect(finalCase).toBeTruthy();
    }
  );
});
